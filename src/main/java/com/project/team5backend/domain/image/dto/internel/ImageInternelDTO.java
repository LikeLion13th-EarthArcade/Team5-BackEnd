package com.project.team5backend.domain.image.dto.internel;

import lombok.Builder;

import java.time.LocalDateTime;

public class ImageInternelDTO {
    @Builder
    public record ImageTrackingResDTO (
            String fileKey,
            String email,
            LocalDateTime createAt
    ){}
}
