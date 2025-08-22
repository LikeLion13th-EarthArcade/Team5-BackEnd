package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionSort;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CENTER_LAT = 37.5665;
    private static final double SEOUL_CENTER_LNG = 126.9780;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final InteractLogService interactLogService;
    @Override
    public ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprove(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // ai 분석을 위한 로그 생성
        interactLogService.logClick(1L, exhibitionId);
        // 전시 이미지들의 fileKey만 조회
        List<String> imageFileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(exhibitionId);

        return ExhibitionConverter.toDetailExhibitionResDTO(exhibition, imageFileKeys);
    }

    @Override
    public ExhibitionResDTO.SearchExhibitionPageResDTO searchExhibition(
            Category category, String district, Mood mood, LocalDate localDate, ExhibitionSort sort, int page) {

        log.info("전시 검색 - category: {}, district: {}, mood: {}, localDate: {}, sort: {}, page: {}",
                category, district, mood, localDate, sort, page);

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 동적 쿼리로 전시 검색
        Page<Exhibition> exhibitionPage = exhibitionRepository.findExhibitionsWithFilters(
                category, district, mood, localDate, sort, pageable);

        // 검색 결과를 DTO로 변환 - Converter 사용
        List<ExhibitionResDTO.SearchExhibitionResDTO> items = exhibitionPage.getContent().stream()
                .map(ExhibitionConverter::toSearchExhibitionResDTO)
                .toList();

        // PageInfo와 MapInfo 생성 - Converter 사용
        return ExhibitionConverter.toSearchExhibitionPageResDTO(
                items, exhibitionPage, SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }

    @Override
    public ExhibitionResDTO.HotNowExhibitionResDTO getHotNowExhibition() {
        LocalDate currentDate = LocalDate.now();
        Pageable topOne = PageRequest.of(0, 1);

        List<Exhibition> exhibitions = exhibitionRepository.findHotNowExhibition(currentDate, topOne, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        Exhibition hotNowEx = exhibitions.get(0);

        List<String> fileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(hotNowEx.getId());
        return ExhibitionConverter.toHotNowExhibitionResDTO(hotNowEx.getId(), hotNowEx.getTitle(), fileKeys);
    }

    @Override
    public ExhibitionResDTO.UpcomingPopularityExhibitionResDTO getUpcomingPopularExhibition() {
        LocalDate currentDate = LocalDate.now();

        Pageable topOne = PageRequest.of(0, 1);
        List<Exhibition> exhibitions = exhibitionRepository.findUpcomingPopularExhibition(currentDate, topOne, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        Exhibition upcomingEx = exhibitions.get(0);

        List<String> fileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(upcomingEx.getId());
        return ExhibitionConverter.toUpcomingPopularityExhibitionResDTO(upcomingEx.getId(), upcomingEx.getTitle(), fileKeys);
    }

    @Override
    public ExhibitionResDTO.PopularRegionExhibitionListResDTO getPopularRegionExhibitions() {
        LocalDate currentDate = LocalDate.now();

        Pageable topFour = PageRequest.of(0, 4);
        List<Exhibition> exhibitions = exhibitionRepository.findTopByDistrict(currentDate, topFour, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        } else if (exhibitions.size() < 4) {
            log.info("size : {}", exhibitions.size());
            throw new ExhibitionException(ExhibitionErrorCode.DIFFERENT_EXHIBITION_NOT_FOUND);
        }
        List<ExhibitionResDTO.PopularRegionExhibitionResDTO> popularRegionExhibitionResDTOs = exhibitions.stream()
                .map(ExhibitionConverter::toPopularRegionExhibitionResDTO)
                .toList();

        return ExhibitionConverter.toPopularRegionExhibitionListResDTO(popularRegionExhibitionResDTOs);
    }

    @Override
    public List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtiePicks() {
        LocalDate today = LocalDate.now();

        List<Exhibition> candidates = exhibitionRepository.findUnpopularCandidates(today, 20); // 후보 20개

        // 날짜 기반 고정 셔플(하루 동안 결과 고정)
        long seed = today.toEpochDay(); // 필요하면 +고정 salt
        java.util.Random r = new java.util.Random(seed);
        java.util.Collections.shuffle(candidates, r);

        return candidates.stream()
                .limit(4)
                .map(ExhibitionConverter::toArtieRecommendationResDTO)
                .toList();
    }

}
