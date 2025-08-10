package com.project.team5backend.domain.image.dto.response;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


public class ImageResDTO {
    @Builder
    public record PresignedUrlResDTO (
            String presignedUrl,
            String fileKey
    ){
    }

    @Builder
    public record DeleteImageResDTO(
            String fileKey,
            boolean isDeleted,
            LocalDateTime deletedAt
    ){}
}
