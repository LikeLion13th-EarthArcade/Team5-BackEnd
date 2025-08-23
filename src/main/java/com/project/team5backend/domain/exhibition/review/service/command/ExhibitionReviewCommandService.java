package com.project.team5backend.domain.exhibition.review.service.command;

import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExhibitionReviewCommandService {
    void createExhibitionReview(Long exhibitionId, String email, ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO, List<MultipartFile> images);

    void deleteExhibitionReview(Long exhibitionReviewId, String email);
}
