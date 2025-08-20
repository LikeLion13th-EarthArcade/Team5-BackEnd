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
}
