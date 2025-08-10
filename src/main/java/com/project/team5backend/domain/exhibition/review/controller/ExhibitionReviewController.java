package com.project.team5backend.domain.exhibition.review.controller;

import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.service.command.ExhibitionReviewCommandService;
import com.project.team5backend.domain.exhibition.review.service.query.ExhibitionReviewQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "전시리뷰 API")
public class ExhibitionReviewController {
    private final ExhibitionReviewCommandService exReviewCommandService;
    private final ExhibitionReviewQueryService exReviewQueryService;


    @Operation(summary = "리뷰 생성", description = "리뷰 생성 api")
    @PostMapping("/{exhibitionId}/reviews")
    public CustomResponse<String> createExhibitionReview(
            @PathVariable Long exhibitionId,
            @Valid @RequestBody ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO) {
        String email = "likelion@naver.com";
        exReviewCommandService.createExhibitionReview(2L, email, createExReviewReqDTO);
        return CustomResponse.onSuccess("해당 전시 리뷰가 생성되었습니다.");
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 상세 조회 api")
    @GetMapping("/reviews/{reviewId}")
    public CustomResponse<ExhibitionReviewResDTO.exReviewDetailResDTO> getExhibitionReviewDetail(
            @PathVariable("reviewId") Long exhibitionReviewId){
        return CustomResponse.onSuccess(exReviewQueryService.getExhibitionReviewDetail(exhibitionReviewId));
    }
    @Operation(summary = "리뷰 삭제", description = "리뷰 소프트 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public CustomResponse<String> deleteExhibitionReview(@PathVariable("reviewId") Long exhibitionReviewId) {
        exReviewCommandService.deleteExhibitionReview(exhibitionReviewId);
        return CustomResponse.onSuccess("해당 전시 리뷰가 삭제되었습니다.");
    }
}
