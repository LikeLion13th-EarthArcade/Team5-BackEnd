package com.project.team5backend.domain.exhibition.review.converter;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.user.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;


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
    public static ExhibitionReviewResDTO.exReviewDetailResDTO toDetailExReviewResDTO(ExhibitionReview review, List<String> fileKeys) {
        return ExhibitionReviewResDTO.exReviewDetailResDTO.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrls(fileKeys)
                .createdAt(review.getCreatedAt())
                .userName(review.getUser().getName())
                .build();
    }
}
