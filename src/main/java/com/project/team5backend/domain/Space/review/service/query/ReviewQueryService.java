package com.project.team5backend.domain.space.review.service.query;


import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewQueryService {
    List<ReviewResponse.ReviewListResponse> getReviewsBySpaceId(Long spaceId);
    ReviewResponse.ReviewDetailResponse getReviewById(Long reviewId);
}