package com.project.team5backend.domain.space.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonResponse<T> {
        private String status;
        private String message;
        private T data;
    }

    @Getter
    @Setter
    public static class ReviewListResponse {
        private Long reviewId;
        private String name;
        private Double rating;
        private String content;
        private String thumbnailImageUrl;
        private int imageCount;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    public static class ReviewDetailResponse {
        private Long reviewId;
        private String name;
        private Double rating;
        private String content;
        private List<String> images;
        private LocalDateTime createdAt;
    }
}