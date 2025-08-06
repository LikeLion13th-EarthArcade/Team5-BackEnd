package com.project.team5backend.domain.exhibition.converter;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.Status;
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
}
