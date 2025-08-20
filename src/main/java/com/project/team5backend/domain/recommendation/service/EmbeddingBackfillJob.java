package com.project.team5backend.domain.recommendation.service;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.recommendation.repository.ExhibitionEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingBackfillJob {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionEmbeddingRepository embRepo;
    private final EmbeddingService embeddingService;

    // 매일 00:30 (누락건만 보정)
    @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
    public void backfillApprovedWithoutEmbedding() {
        // 간단하게: 최근 3일 승인 전시 중 임베딩 없는 것
        var threeDaysAgo = java.time.LocalDate.now().minusDays(3);
        List<Exhibition> candidates = exhibitionRepository.findAll().stream()
                .filter(e -> e.getStatus() == Status.APPROVED)
                .filter(e -> e.getCreatedAt() == null || !e.getCreatedAt().toLocalDate().isBefore(threeDaysAgo))
                .filter(e -> !embRepo.existsById(e.getId()))
                .toList();

        for (Exhibition e : candidates) {
            embeddingService.upsertExhibitionEmbedding(e);
        }
        log.info("[EmbeddingBackfill] 완료, processed={}", candidates.size());
    }
}