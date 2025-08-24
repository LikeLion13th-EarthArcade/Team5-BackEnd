package com.project.team5backend.domain.user.me.converter;


import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.ExhibitionLike;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.entity.Activity;

import java.util.List;
import java.util.stream.Collectors;


public class UserMeConverter {

    // -------------------- Activity 관련 --------------------
    public static UserMeResponse.UserActivityResponse.ExhibitionLike toExhibitionLike(ExhibitionLike entity) {
        return new UserMeResponse.UserActivityResponse.ExhibitionLike(
                entity.getId(),
                entity.getExhibition().getTitle(),
                entity.getExhibition().getType().name(),
                entity.getExhibition().getMood().name(),
                entity.getExhibition().getThumbnail()
        );
    }

    public static UserMeResponse.UserActivityResponse.SpaceLike toSpaceLike(SpaceLike entity) {
        Space space = entity.getSpace();
        return new UserMeResponse.UserActivityResponse.SpaceLike(
                entity.getId(),
                entity.getSpace().getName(),
                entity.getSpace().getLocation(),
                (space.getImageUrls() != null && !space.getImageUrls().isEmpty())
                        ? space.getImageUrls().get(0)
                        : null
        );
    }

    // 기존 공간 리뷰 변환
    public static UserMeResponse.UserActivityResponse.Review toReview(Review entity) {
        return new UserMeResponse.UserActivityResponse.Review(
                entity.getId(),
                entity.getUser().getName(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    // 전시 리뷰 변환
    public static UserMeResponse.UserActivityResponse.Review toExhibitionReview(ExhibitionReview entity) {
        return new UserMeResponse.UserActivityResponse.Review(
                entity.getId(),
                entity.getUser().getName(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }


    public static UserMeResponse.UserActivityResponse toUserActivityResponse(
            List<ExhibitionLike> exhibitionLikes,
            List<SpaceLike> spaceLikes,
            List<UserMeResponse.UserActivityResponse.Review> reviews
    ) {
        return new UserMeResponse.UserActivityResponse(
                exhibitionLikes.stream().map(UserMeConverter::toExhibitionLike).toList(),
                spaceLikes.stream().map(UserMeConverter::toSpaceLike).toList(),
                reviews
        );
    }

    // -------------------- Space 관련 --------------------
    public static UserMeResponse.UserSpacesResponse.Space toSpaceDto(Space space) {
        return new UserMeResponse.UserSpacesResponse.Space(
                space.getId(),
                space.getName(),
                space.getLocation()
        );
    }

    public static UserMeResponse.UserSpacesResponse toUserSpacesResponse(List<Space> spaces) {
        return new UserMeResponse.UserSpacesResponse(
                spaces.stream().map(UserMeConverter::toSpaceDto).collect(Collectors.toList())
        );
    }

    // -------------------- Exhibition 관련 --------------------
    public static UserMeResponse.UserExhibitionsResponse.Exhibition toExhibitionDto(Exhibition exhibition) {
        return new UserMeResponse.UserExhibitionsResponse.Exhibition(
                exhibition.getId(),
                exhibition.getTitle(),
                exhibition.getStatus().name()
        );
    }

    public static UserMeResponse.UserExhibitionsResponse toUserExhibitionsResponse(List<Exhibition> exhibitions) {
        return new UserMeResponse.UserExhibitionsResponse(
                exhibitions.stream().map(UserMeConverter::toExhibitionDto).collect(Collectors.toList())
        );
    }

    // -------------------- Reservation 관련 --------------------
    public static UserMeResponse.UserReservationsResponse.Reservation toReservationDto(Reservation reservation) {
        return new UserMeResponse.UserReservationsResponse.Reservation(
                reservation.getId(),
                reservation.getSpace().getName(),
                reservation.getStatus().name(),
                reservation.getReservedAt()
        );
    }

    public static UserMeResponse.UserReservationsResponse toUserReservationsResponse(List<Reservation> reservations) {
        return new UserMeResponse.UserReservationsResponse(
                reservations.stream()
                        .map(UserMeConverter::toReservationDto)
                        .collect(Collectors.toList())
        );
    }
}
