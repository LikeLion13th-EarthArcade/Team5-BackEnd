package com.project.team5backend.domain.recommendation.service;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.recommendation.dto.KeyScoreRow;
import com.project.team5backend.domain.recommendation.dto.response.RecommendResDTO;
import com.project.team5backend.domain.recommendation.entity.ExhibitionEmbedding;
import com.project.team5backend.domain.recommendation.repository.ExhibitionEmbeddingRepository;
import com.project.team5backend.domain.recommendation.repository.ExhibitionInteractLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ExhibitionInteractLogRepository logRepo;
    private final ExhibitionRepository exhibitionRepo;
    private final ExhibitionEmbeddingRepository embRepo;
    private final ExhibitionLikeRepository likeRepo;

    private static final int LIMIT = 4;
    private static final int WINDOW_DAYS = 90;

    private record Keys(Category cat, Mood mood, boolean eligible) {}

    private Keys computeKeys(Long userId){
        if (userId == null) return new Keys(null,null,false);
        var since = LocalDateTime.now().minusDays(WINDOW_DAYS);
        if (logRepo.countSince(userId, since) == 0) return new Keys(null,null,false);

        KeyScoreRow cRow = logRepo.topCategory(userId, since);
        KeyScoreRow mRow = logRepo.topMood(userId, since);
        if (cRow == null && mRow == null) return new Keys(null,null,false);

        Category cat = (cRow != null) ? Category.valueOf(cRow.getKeyName()) : null;
        Mood mood = (mRow != null) ? Mood.valueOf(mRow.getKeyName()) : null;
        return new Keys(cat, mood, true);
    }

    // 홈 요약(프리뷰 1개)
    public RecommendResDTO.PersonalizedSummaryResDTO summary(Long userId) {
        var k = computeKeys(userId);
        if (!k.eligible()) return new RecommendResDTO.PersonalizedSummaryResDTO(false, null, null, null);

        List<Exhibition> top4 = recommendWithEmbedding(userId, k, 4);
        if (top4.isEmpty()) return new RecommendResDTO.PersonalizedSummaryResDTO(false, null, null, null);

        Exhibition e = top4.get(0);
        return new RecommendResDTO.PersonalizedSummaryResDTO(true, e.getId(), e.getTitle(), e.getThumbnail());
    }

    // 상세(4개 + 키워드 라벨)
    public RecommendResDTO.PersonalizedDetailResDTO detail(Long userId) {
        var k = computeKeys(userId);
        if (!k.eligible()) return new RecommendResDTO.PersonalizedDetailResDTO(null, null, List.of());

        // 4개 추천(엔티티)
        List<Exhibition> top = recommendWithEmbedding(userId, k, LIMIT);

        // 로그인 유저의 좋아요 세트 한 번에 조회 (N+1 방지)
        Set<Long> liked;
        if (userId != null && !top.isEmpty()) {
            var ids = top.stream().map(Exhibition::getId).toList();
            liked = new java.util.HashSet<>(likeRepo.findLikedExhibitionIds(userId, ids));
        } else {
            liked = java.util.Collections.emptySet();
        }

        // 메서드 참조 대신 람다로 isLiked 전달
        var items = top.stream()
                .map(e -> ExhibitionConverter.toCard(e, liked.contains(e.getId())))
                .toList();

        return new RecommendResDTO.PersonalizedDetailResDTO(
                k.cat(),
                k.mood(),
                items
        );
    }

    // ----- 핵심: 임베딩 재랭킹 -----
    private List<Exhibition> recommendWithEmbedding(Long userId, Keys k, int topK) {
        var today = LocalDate.now();
        // 후보는 넉넉히 (최소 100~200) 뽑아야 리랭킹이 작동합니다.
        var candidates = exhibitionRepo.recommendByKeywords(
                k.cat(), k.mood(), Status.APPROVED, today, PageRequest.of(0, 200)
        );
        if (candidates.isEmpty()) return List.of();

        var since = LocalDateTime.now().minusDays(WINDOW_DAYS);
        var recentIds = logRepo.findRecentExhibitionIds(userId, since, 50);
        float[] userVec = averageEmbedding(embRepo.findByExhibitionIdIn(recentIds));
        if (userVec == null) {
            // 유저 벡터 없으면 기본 정렬로 후처리만 적용
            List<Exhibition> sortedByDefault = candidates; // 이미 repo가 정렬해 주는 경우
            if (k.cat() != null && k.mood() != null) {
                return Reranker.enforceAtLeastNAndMatches(
                        sortedByDefault, topK, 2,
                        Exhibition::getId, Exhibition::getCategory, Exhibition::getMood,
                        k.cat(), k.mood()
                );
            }
            return sortedByDefault.stream().limit(topK).toList();
        }

        var candIds = candidates.stream().map(Exhibition::getId).toList();
        Map<Long, float[]> embMap = embRepo.findByExhibitionIdIn(candIds).stream()
                .collect(Collectors.toMap(ExhibitionEmbedding::getExhibitionId, ExhibitionEmbedding::toArray));

        // 리뷰 점수 정규화
        double minR = Double.POSITIVE_INFINITY, maxR = Double.NEGATIVE_INFINITY;
        Map<Long, Double> rScore = new HashMap<>();
        for (var e : candidates) {
            double r = (e.getReviewCount()==null?0:e.getReviewCount())
                    + (e.getTotalReviewScore()==null?0:e.getTotalReviewScore());
            rScore.put(e.getId(), r);
            if (r < minR) minR = r;
            if (r > maxR) maxR = r;
        }
        final double W_SIM = 0.7, W_REV = 0.3;
        final double finalMaxR = maxR, finalMinR = minR;

        // 1) 전체 후보를 점수 내림차순으로 정렬(여기서 limit 걸지 말 것)
        List<Exhibition> sortedByScoreDesc = candidates.stream()
                .map(e -> {
                    float[] v = embMap.get(e.getId());
                    double sim = (v == null) ? 0 : cosine(userVec, v);
                    double revNorm = (finalMaxR > finalMinR)
                            ? (rScore.get(e.getId()) - finalMinR) / (finalMaxR - finalMinR)
                            : 0.0;
                    double score = W_SIM * sim + W_REV * revNorm;
                    return Map.entry(e, score);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // 2) 리랭킹 적용: 4개 중 ≥2개는 (topCategory AND topMood)
        if (k.cat() != null && k.mood() != null) {
            return Reranker.enforceAtLeastNAndMatches(
                    sortedByScoreDesc, topK, 2,
                    Exhibition::getId, Exhibition::getCategory, Exhibition::getMood,
                    k.cat(), k.mood()
            );
        }

        // (topCat/mood가 하나라도 없으면 리랭킹 skip)
        return sortedByScoreDesc.stream().limit(topK).toList();
    }

    private float[] averageEmbedding(List<ExhibitionEmbedding> list){
        if (list == null || list.isEmpty()) return null;
        float[] sum = null; int n = 0;
        for (var ee : list){
            float[] v = ee.toArray();
            if (sum == null) sum = new float[v.length];
            for (int i=0;i<v.length;i++) sum[i] += v[i];
            n++;
        }
        for (int i=0;i<sum.length;i++) sum[i] /= n;
        return sum;
    }
    private double cosine(float[] a, float[] b){
        if (a==null || b==null || a.length!=b.length) return 0;
        double dot=0, na=0, nb=0;
        for (int i=0;i<a.length;i++){ dot += a[i]*b[i]; na += a[i]*a[i]; nb += b[i]*b[i]; }
        if (na==0 || nb==0) return 0;
        return dot / (Math.sqrt(na)*Math.sqrt(nb));
    }
}
