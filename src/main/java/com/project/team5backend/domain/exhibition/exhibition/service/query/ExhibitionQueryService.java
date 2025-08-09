package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;

public interface ExhibitionQueryService {

    ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long id);
}
