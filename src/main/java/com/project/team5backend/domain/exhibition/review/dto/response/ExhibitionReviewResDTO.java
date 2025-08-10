package com.project.team5backend.domain.exhibition.review.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionReviewResDTO {
    @Builder
    public record exReviewPreviewResDTO(
            Long reviewId,
            String name,
            String content,
            List<String> imageUrls,
            LocalDate createdAt
    ) {}
}
