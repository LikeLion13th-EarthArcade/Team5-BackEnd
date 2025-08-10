package com.project.team5backend.domain.exhibition.review.service.query;

import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;

public interface ExhibitionReviewQueryService {
    ExhibitionReviewResDTO.exReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId);
}
