package com.project.team5backend.domain.exhibition.review.service.command;

import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;

public interface ExhibitionReviewCommandService {
    void createExhibitionReview(Long exhibitionId, String email, ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO);

    void deleteExhibitionReview(Long exhibitionReviewId, String email);
}
