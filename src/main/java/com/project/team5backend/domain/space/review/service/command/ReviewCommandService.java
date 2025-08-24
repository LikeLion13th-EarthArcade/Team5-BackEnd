package com.project.team5backend.domain.space.review.service.command;


import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewCommandService {
    void createReview(Long spaceId, Long userId, ReviewRequest.CreateRe request, List<MultipartFile> images);
    void deleteReview(Long reviewId, Long userId);
}