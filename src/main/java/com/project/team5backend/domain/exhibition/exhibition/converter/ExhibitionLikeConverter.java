package com.project.team5backend.domain.exhibition.exhibition.converter;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.ExhibitionLike;
import com.project.team5backend.domain.user.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionLikeConverter {

    public static ExhibitionLike toEntity (User user, Exhibition exhibition) {
        return ExhibitionLike.builder()
                .user(user)
                .exhibition(exhibition)
                .build();
    }

    public static ExhibitionResDTO.LikeExhibitionResDTO toLikeExhibitionResDTO(Long exhibitionId, String message) {
        return ExhibitionResDTO.LikeExhibitionResDTO.builder()
                .exhibitionId(exhibitionId)
                .message(message)
                .build();
    }

}
