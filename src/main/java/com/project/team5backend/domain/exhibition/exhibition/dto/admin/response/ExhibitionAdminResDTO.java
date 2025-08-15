package com.project.team5backend.domain.exhibition.exhibition.dto.admin.response;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import lombok.Builder;

public class ExhibitionAdminResDTO {
    @Builder
    public record PendingExhibitionResDTO (
            Long exhibitionId,
            String title,
            String thumbnail,
            Status status
    ){}
}
