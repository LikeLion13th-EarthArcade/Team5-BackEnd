package com.project.team5backend.domain.space.service.command;


import com.project.team5backend.domain.space.dto.request.ReviewRequest;

public interface ReviewCommandService {
    void createReview(Long spaceId, Long userId, ReviewRequest.Create request);
    void deleteReview(Long reviewId, Long userId);
}