package com.project.team5backend.domain.space.controller;


import com.project.team5backend.domain.space.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.service.command.ReviewCommandService;
import com.project.team5backend.domain.space.service.query.ReviewQueryService;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewResponse.CommonResponse<Void>> createReview(
            @PathVariable Long spaceId,
            @RequestBody ReviewRequest.Create request) {
        //SecurityContext에서 Authentication 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentication 객체에서 CustomUserDetails를 가져옴
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // CustomUserDetails에서 userId를 가져옴
        Long userId = userDetails.getUserId();
        reviewCommandService.createReview(spaceId, userId, request);
        return ResponseEntity.ok(new ReviewResponse.CommonResponse<>("200", "전시 공간 리뷰 등록에 성공하였습니다.", null));
    }

    @Operation(summary = "전시 공간 리뷰 목록 조회")
    @GetMapping
    public ResponseEntity<ReviewResponse.CommonResponse<List<ReviewResponse.ReviewListResponse>>> getReviews(@PathVariable Long spaceId) {
        List<ReviewResponse.ReviewListResponse> reviews = reviewQueryService.getReviewsBySpaceId(spaceId);
        return ResponseEntity.ok(new ReviewResponse.CommonResponse<>("200", "전시 공간 리뷰 목록 조회에 성공하였습니다.", reviews));
    }
    @Operation(summary = "전시 공간 리뷰 상세 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse.CommonResponse<ReviewResponse.ReviewDetailResponse>> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponse.ReviewDetailResponse reviewDetail = reviewQueryService.getReviewById(reviewId);
        return ResponseEntity.ok(new ReviewResponse.CommonResponse<>("200", "전시 공간 리뷰 상세 조회에 성공하였습니다.", reviewDetail));
    }

    @Operation(summary = "전시 공간 리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse.CommonResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        reviewCommandService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(new ReviewResponse.CommonResponse<>("200", "전시 공간 리뷰 삭제에 성공하였습니다.", null));
    }
}