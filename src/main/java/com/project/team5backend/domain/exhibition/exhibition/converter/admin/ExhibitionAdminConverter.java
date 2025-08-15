package com.project.team5backend.domain.exhibition.exhibition.converter.admin;

import com.project.team5backend.domain.exhibition.exhibition.dto.admin.response.ExhibitionAdminResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionAdminConverter {

    public static ExhibitionAdminResDTO.PendingExhibitionResDTO toPendingExhibitionResDTO (Exhibition exhibition) {
        return ExhibitionAdminResDTO.PendingExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .status(exhibition.getStatus())
                .build();
    }
}
