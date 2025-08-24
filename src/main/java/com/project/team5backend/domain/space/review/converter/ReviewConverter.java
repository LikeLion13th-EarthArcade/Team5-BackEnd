package com.project.team5backend.domain.space.review.converter;

import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewConverter {

    public Review toReview(ReviewRequest.CreateRe request, Space space, User user, String mainImageUrl) {
        Review review = new Review();
        review.setRating(request.rating());
        review.setContent(request.content());
        review.setMainImageKey(mainImageUrl);  // upload()에서 반환된 URL
        review.setSpace(space);
        review.setUser(user);
        return review;
    }

    public ReviewResponse.ReviewListResponse toReviewListResponse(Review review) {
        String thumbnailUrl = review.getMainImageKey();  // S3 URL 그대로 사용

        return new ReviewResponse.ReviewListResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                thumbnailUrl,
                review.getImageUrls().size(),
                review.getCreatedAt()
        );
    }

    public List<ReviewResponse.ReviewListResponse> toReviewListResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toReviewListResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse.ReviewDetailResponse toReviewDetailResponse(Review review) {
        List<String> imageUrls = review.getImageUrls(); // 이미 S3 URL 저장됨

        return new ReviewResponse.ReviewDetailResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                imageUrls,
                review.getCreatedAt()
        );
    }
}
