package com.project.team5backend.domain.space.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewResponse {

    public record ReviewListResponse(
            Long reviewId,
            String name,
            Double rating,
            String content,
            String thumbnailImageUrl,
            int imageCount,
            LocalDateTime createdAt
    ){}
    public record ReviewDetailResponse(
            Long reviewId,
            String name,
            Double rating,
            String content,
            List<String> images,
            LocalDateTime createdAt
    ){}
}