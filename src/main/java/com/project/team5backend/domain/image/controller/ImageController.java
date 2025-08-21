package com.project.team5backend.domain.image.controller;

import com.project.team5backend.domain.image.dto.request.ImageReqDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@Tag(name = "이미지 API")
public class ImageController {

    private final ImageCommandService imageCommandService;
    private final RedisImageTracker redisImageTracker;

    @Operation(method = "POST", summary = "이미지 업로드", description = "presigned url과 fileKey 발급용, 업로드 하는건 아님")
    @PostMapping("/presigned")
    public CustomResponse<ImageResDTO.PresignedUrlResDTO> uploadImage (
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ImageReqDTO.PresignedUrlReqDTO presignedUrlDTO) {
        ImageResDTO.PresignedUrlResDTO presignedUrlResDTO = imageCommandService.generatePresignedUrl(userDetails.getEmail(), presignedUrlDTO); // presignedUrl 발급
        return CustomResponse.onSuccess(presignedUrlResDTO);
    }

    @Operation(method = "DELETE", summary = "이미지 삭제", description = "XX 등록 전에 선택했던 이미지 삭제할때, redis + s3에서 삭제")
    @DeleteMapping
    public CustomResponse<ImageResDTO.DeleteImageResDTO> deleteImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("fileKey") String fileKey
    ) {
        String email = userDetails.getEmail();
        if (!redisImageTracker.isOwnedByUser(email, fileKey)) {
            throw new ImageException(ImageErrorCode.IMAGE_UNAUTHORIZED);
        }
        return CustomResponse.onSuccess(imageCommandService.delete(email, fileKey));
    }

    @Operation(method = "DELETE", summary = "이미지 업로드 전 쓰레기 이미지 정리", description = "항상 XX(전시,리뷰,공간) 등록 화면에 들어갈 때 실행해야함.")
    @DeleteMapping("/trash/clear")
    public CustomResponse<String> clearTrashImages(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        imageCommandService.clearTrackingByEmail(userDetails.getEmail());
        return CustomResponse.onSuccess("쓰레기 키파일 정리 완료");
    }
}
