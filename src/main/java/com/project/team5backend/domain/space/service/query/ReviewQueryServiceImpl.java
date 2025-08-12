package com.project.team5backend.domain.space.service.query;


import com.project.team5backend.domain.space.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.entity.Review;
import com.project.team5backend.domain.space.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl implements ReviewQueryService {
    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewResponse.ReviewListResponse> getReviewsBySpaceId(Long spaceId) {
        List<Review> reviews = reviewRepository.findBySpaceId(spaceId);
        return reviews.stream()
                .map(this::toReviewListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse.ReviewDetailResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        return toReviewDetailResponse(review);
    }

    private ReviewResponse.ReviewListResponse toReviewListResponse(Review review) {
        ReviewResponse.ReviewListResponse response = new ReviewResponse.ReviewListResponse();
        response.setReviewId(review.getId());
        response.setName(review.getUser().getName()); // User 엔티티에서 이름 가져옴
        response.setRating(review.getRating());
        response.setContent(review.getContent());
        response.setCreatedAt(review.getCreatedAt());
        response.setImageCount(review.getImageUrls().size());
        if (!review.getImageUrls().isEmpty()) {
            response.setThumbnailImageUrl(review.getImageUrls().get(0));
        }
        return response;
    }

    private ReviewResponse.ReviewDetailResponse toReviewDetailResponse(Review review) {
        ReviewResponse.ReviewDetailResponse response = new ReviewResponse.ReviewDetailResponse();
        response.setReviewId(review.getId());
        response.setName(review.getUser().getName()); // User 엔티티에서 이름 가져옴
        response.setRating(review.getRating());
        response.setContent(review.getContent());
        response.setImages(review.getImageUrls());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}