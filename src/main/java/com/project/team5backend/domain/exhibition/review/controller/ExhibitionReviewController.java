package com.project.team5backend.domain.exhibition.review.controller;

import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.service.command.ExhibitionReviewCommandService;
import com.project.team5backend.domain.exhibition.review.service.query.ExhibitionReviewQueryService;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "전시리뷰 API")
public class ExhibitionReviewController {
    private final ExhibitionReviewCommandService exReviewCommandService;
    private final ExhibitionReviewQueryService exReviewQueryService;


    @SwaggerBody(content = @Content(
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
                    @Encoding(name = "images", contentType = "image/*")
            }
    ))
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "리뷰 생성", description = "리뷰 생성 API")
    public CustomResponse<String> createExhibitionReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long exhibitionId,
            @RequestPart("request") @Valid ExhibitionReviewReqDTO.createExReviewReqDTO request,
            @RequestPart("images") List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        if (images.size() > 5) {
            throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);
        }

        exReviewCommandService.createExhibitionReview(exhibitionId, userDetails.getEmail(), request, images);
        return CustomResponse.onSuccess("해당 전시 리뷰가 생성되었습니다.");
    }


    @Operation(summary = "리뷰 목록 조회", description = "리뷰 목록 조회 api")
    @GetMapping("{exhibitionId}/reviews")
    public CustomResponse<Page<ExhibitionReviewResDTO.exReviewDetailResDTO>> getExhibitionReviews(
            @PathVariable Long exhibitionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ExhibitionReviewResDTO.exReviewDetailResDTO> reviewPage = exReviewQueryService.getExhibitionReviewList(exhibitionId, pageable);
        return CustomResponse.onSuccess(reviewPage);
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 상세 조회 api")
    @GetMapping("/reviews/{reviewId}")
    public CustomResponse<ExhibitionReviewResDTO.exReviewDetailResDTO> getExhibitionReviewDetail(
            @PathVariable("reviewId") Long exhibitionReviewId){
        return CustomResponse.onSuccess(exReviewQueryService.getExhibitionReviewDetail(exhibitionReviewId));
    }
    @Operation(summary = "리뷰 삭제", description = "리뷰 소프트 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public CustomResponse<String> deleteExhibitionReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("reviewId") Long exhibitionReviewId) {
        exReviewCommandService.deleteExhibitionReview(exhibitionReviewId, userDetails.getEmail());
        return CustomResponse.onSuccess("해당 전시 리뷰가 삭제되었습니다.");
    }
}
