package com.project.team5backend.domain.exhibition.review.service.query;

import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionReviewQueryService {
    ExhibitionReviewResDTO.exReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId);

    Page<ExhibitionReviewResDTO.exReviewDetailResDTO> getExhibitionReviewList(Long exhibitionId, Pageable pageable);
}
