package com.project.team5backend.domain.space.review.controller;


import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.service.command.ReviewCommandService;
import com.project.team5backend.domain.space.review.service.query.ReviewQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @Operation(summary = "전시 공간 리뷰 등록")
    @PostMapping
    public CustomResponse<Void> createReview(
            @PathVariable Long spaceId,
            @RequestBody ReviewRequest.CreateRe request) {
        //SecurityContext에서 Authentication 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentication 객체에서 CustomUserDetails를 가져옴
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // CustomUserDetails에서 userId를 가져옴
        Long userId = userDetails.getUserId();
        reviewCommandService.createReview(spaceId, userId, request);
        return CustomResponse.onSuccess(null);
    }

    @Operation(summary = "전시 공간 리뷰 목록 조회")
    @GetMapping
    public CustomResponse<List<ReviewResponse.ReviewListResponse>> getReviews(@PathVariable Long spaceId) {
        List<ReviewResponse.ReviewListResponse> reviews = reviewQueryService.getReviewsBySpaceId(spaceId);
        return CustomResponse.onSuccess(reviews);
    }
    @Operation(summary = "전시 공간 리뷰 상세 조회")
    @GetMapping("/{reviewId}")
    public CustomResponse<ReviewResponse.ReviewDetailResponse> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponse.ReviewDetailResponse reviewDetail = reviewQueryService.getReviewById(reviewId);
        return CustomResponse.onSuccess(reviewDetail);
    }

    @Operation(summary = "전시 공간 리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public CustomResponse<Void> deleteReview(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        reviewCommandService.deleteReview(reviewId, userId);
        return CustomResponse.onSuccess(null);
    }
}