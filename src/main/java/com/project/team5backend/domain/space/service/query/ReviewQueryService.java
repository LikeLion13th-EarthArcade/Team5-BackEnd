package com.project.team5backend.domain.space.service.query;


import com.project.team5backend.domain.space.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewQueryService {
    List<ReviewResponse.ReviewListResponse> getReviewsBySpaceId(Long spaceId);
    ReviewResponse.ReviewDetailResponse getReviewById(Long reviewId);
}