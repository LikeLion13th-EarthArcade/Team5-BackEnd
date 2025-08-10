package com.project.team5backend.domain.exhibition.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;


public class ExhibitionReviewReqDTO {
    @Builder
    public record createExReviewReqDTO(
            @DecimalMax(value = "5.0", message = "평점은 최대 5.0점까지 가능합니다")
            @DecimalMin(value = "0.0", message = "평점은 최소 0.0점부터 가능합니다")
            Double rating,
            String content
    ) {}
}
