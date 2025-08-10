package com.project.team5backend.domain.exhibition.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionReviewResDTO {
    @Builder
    public record exReviewDetailResDTO(
            Long reviewId,
            Double rating,
            String content,
            List<String> imageUrls,
            LocalDateTime createdAt,
            String userName
    ) {}

}
