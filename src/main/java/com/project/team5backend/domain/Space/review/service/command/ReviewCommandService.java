package com.project.team5backend.domain.space.review.service.command;


import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;

public interface ReviewCommandService {
    void createReview(Long spaceId, Long userId, ReviewRequest.CreateRe request);
    void deleteReview(Long reviewId, Long userId);
}