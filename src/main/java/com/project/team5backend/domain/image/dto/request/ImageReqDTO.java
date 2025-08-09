package com.project.team5backend.domain.image.dto.request;

import lombok.Builder;

import java.util.List;

public class ImageReqDTO {
    @Builder
    public record PresignedUrlReqDTO(
            String fileExtension,
            String contentType
    ) {
    }

    @Builder
    public record PresignedUrlListReqDTO(
            List<PresignedUrlReqDTO> images // 각 이미지마다 contentType, fileExtension
    ){}
}
