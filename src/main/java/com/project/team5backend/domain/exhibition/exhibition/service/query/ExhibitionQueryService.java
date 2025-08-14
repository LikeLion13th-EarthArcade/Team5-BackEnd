package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionSort;

import java.time.LocalDate;

public interface ExhibitionQueryService {

    ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long id);

    // 전시 검색
    ExhibitionResDTO.SearchExhibitionPageResDTO searchExhibition(Category category, String district, Mood mood, LocalDate date, ExhibitionSort sort, int page);

    // 지금뜨는 전시회
    ExhibitionResDTO.HotNowExhibitionResDTO getHotNowExhibition();

    // 다가오는 인기있는 전시회(좋아요 수)
    ExhibitionResDTO.UpcomingPopularityExhibitionResDTO getUpcomingPopularExhibition();

    // 지금 뜨는 지역별 전시회
    ExhibitionResDTO.PopularRegionExhibitionListResDTO getPopularRegionExhibitions();
}
