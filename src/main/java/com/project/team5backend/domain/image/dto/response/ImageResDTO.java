package com.project.team5backend.domain.image.dto.response;


import lombok.Builder;

import java.util.List;


public class ImageResDTO {
    @Builder
    public record PresignedUrlResDTO (
            String presignedUrl,
            String fileKey
    ){
    }

    @Builder
    public record PresignedUrlListResDTO (
            List<PresignedUrlResDTO> presignedUrls
    ){}

    @Builder
    public record SaveImageResDTO (
            String profileImageUrl,
            String message
    ){
    }
}
