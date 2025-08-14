package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionSort;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
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
    @Override
    public ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long id) {
        Exhibition exhibition = exhibitionRepository.findById(id)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // 전시 이미지들의 fileKey만 조회
        List<String> imageFileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(2L);

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

        List<Exhibition> exhibitions = exhibitionRepository.findHotNowExhibition(currentDate, topOne);
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

        List<Exhibition> exhibitions = exhibitionRepository.findUpcomingPopularExhibition(currentDate);
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

        List<Exhibition> exhibitions = exhibitionRepository.findTopByDistrict(currentDate);
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

}
