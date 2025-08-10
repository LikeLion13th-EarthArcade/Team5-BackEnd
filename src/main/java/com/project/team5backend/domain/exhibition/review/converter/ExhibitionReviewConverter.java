package com.project.team5backend.domain.exhibition.review.converter;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionReviewConverter {

    public static ExhibitionReview toEntity(ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO, Exhibition exhibition, User user) {
        return ExhibitionReview.builder()
                .rating(createExReviewReqDTO.rating())
                .content(createExReviewReqDTO.content())
                .exhibition(exhibition)
                .user(user)
                .isDeleted(false)
                .build();
    }
}
