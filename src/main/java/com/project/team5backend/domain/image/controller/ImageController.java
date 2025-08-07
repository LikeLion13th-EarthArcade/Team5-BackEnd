package com.project.team5backend.domain.image.controller;

import com.project.team5backend.domain.image.dto.request.ImageReqDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
@Tag(name = "이미지 API")
public class ImageController {

    private final ImageCommandService imageCommandService;

    @Operation(method = "POST", summary = "이미지 업로드", description = "presigned url과 fileKey 발급용, 업로드 하는건 아님")
    @PostMapping("/presigned")
    public CustomResponse<ImageResDTO.PresignedUrlResDTO> uploadImage (
             @RequestBody ImageReqDTO.PresignedUrlReqDTO presignedUrlDTO) {
        String email = "likelion@naver.com";
        ImageResDTO.PresignedUrlResDTO presignedUrlResDTO = imageCommandService.generatePresignedUrl(email, presignedUrlDTO); // presignedUrl 발급
        return CustomResponse.onSuccess(presignedUrlResDTO);
    }

    @Operation(method = "DELETE", summary = "이미지 삭제", description = "XX 등록 전에 선택했던 이미지 삭제할때, redis + s3에서 삭제")
    @DeleteMapping
    public CustomResponse<ImageResDTO.DeleteImageResDTO> deleteImage(
            @RequestParam("fileKey") String fileKey
    ) {
        String email = "likelion@naver.com";
        return CustomResponse.onSuccess(imageCommandService.delete(email, fileKey));
    }
}
