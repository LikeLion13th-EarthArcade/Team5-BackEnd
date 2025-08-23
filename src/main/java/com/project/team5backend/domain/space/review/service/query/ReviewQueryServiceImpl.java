package com.project.team5backend.domain.space.review.service.query;


import com.project.team5backend.domain.space.review.converter.ReviewConverter;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.review.repository.ReviewRepository;
import com.project.team5backend.domain.space.review.service.query.ReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl implements ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;

    @Override
    public List<ReviewResponse.ReviewListResponse> getReviewsBySpaceId(Long spaceId) {
        List<Review> reviews = reviewRepository.findBySpaceId(spaceId);
        return reviewConverter.toReviewListResponseList(reviews);
    }

    @Override
    public ReviewResponse.ReviewDetailResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        return reviewConverter.toReviewDetailResponse(review);
    }
}