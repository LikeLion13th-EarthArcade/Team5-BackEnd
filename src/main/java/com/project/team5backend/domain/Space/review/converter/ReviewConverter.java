package com.project.team5backend.domain.space.review.converter;

import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewConverter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Review toReview(ReviewRequest.Create request, Space space, User user) {
        Review review = new Review();
        review.setRating(request.rating());
        review.setContent(request.content());
        review.setImageUrls(request.images());
        review.setSpace(space);
        review.setUser(user);
        return review;
    }

    public ReviewResponse.ReviewListResponse toReviewListResponse(Review review) {
        return new ReviewResponse.ReviewListResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls().isEmpty() ? null : review.getImageUrls().get(0), // thumbnailImageUrl
                review.getImageUrls().size(), // imageCount
                review.getCreatedAt()
        );
    }

    public List<ReviewResponse.ReviewListResponse> toReviewListResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toReviewListResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse.ReviewDetailResponse toReviewDetailResponse(Review review) {
        return new ReviewResponse.ReviewDetailResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getCreatedAt()
        );
    }
}