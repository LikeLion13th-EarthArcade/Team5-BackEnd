package com.project.team5backend.domain.exhibition.exhibition.converter;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.user.entity.User;

public class ExhibitionConverter {

    public static Exhibition toEntity (User user, ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO) {
        return Exhibition.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openingTime(createReqDTO.openingHour())
                .price(createReqDTO.price())
                .homepageUrl(createReqDTO.homepageUrl())
                .status(Status.PENDING)
                .category(createReqDTO.category())
                .type(createReqDTO.type())
                .mood(createReqDTO.mood())
                .facilities(createReqDTO.facility())
                .isDeleted(false)
                .ratingAvg(0)
                .likeCount(0)
                .reviewCount(0)
                .thumbnail(null)
                .address(null)
                .user(user)
                .build();
    }

    public static ExhibitionResDTO.PreviewExhibitionResDTO toPreviewExhibitionResDTO (ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO) {
        return ExhibitionResDTO.PreviewExhibitionResDTO.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openingTime(createReqDTO.openingHour())
                .imageUrls(null)
                .price(createReqDTO.price())
                .homepageUrl(createReqDTO.homepageUrl())
                .price(createReqDTO.price())
                .address(createReqDTO.address())
                .category(createReqDTO.category())
                .type(createReqDTO.type())
                .mood(createReqDTO.mood())
                .facility(createReqDTO.facility())
                .build();
    }
}
