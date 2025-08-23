package com.project.team5backend.domain.user.me.converter;


import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.entity.Activity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMeConverter {

    public static UserMeResponse.UserActivityResponse.ExhibitionLike toExhibitionLike(Activity entity) {
        return new UserMeResponse.UserActivityResponse.ExhibitionLike(
                entity.getActivityId(),
                entity.getTitle(),
                entity.getCreatedAt()
        );
    }

    public static UserMeResponse.UserSpacesResponse.Space toSpaceDto(Space space) {
        return new UserMeResponse.UserSpacesResponse.Space(
                space.getId(),
                space.getName(),
                space.getLocation()
        );
    }

    public static UserMeResponse.UserExhibitionsResponse.Exhibition toExhibitionDto(Exhibition exhibition) {
        return new UserMeResponse.UserExhibitionsResponse.Exhibition(
                exhibition.getId(),
                exhibition.getTitle(),
                exhibition.getStatus().name()
        );
    }

    public static UserMeResponse.UserReservationsResponse.Reservation toReservationDto(Reservation reservation) {
        return new UserMeResponse.UserReservationsResponse.Reservation(
                reservation.getId(),
                reservation.getSpace().getName(),
                reservation.getStatus().name(),
                reservation.getReservedAt()
        );
    }

    public static UserMeResponse.UserActivityResponse toUserActivityResponse(List<Activity> activities) {
        return new UserMeResponse.UserActivityResponse(
                activities.stream().map(UserMeConverter::toExhibitionLike).collect(Collectors.toList()),
                List.of(), // 필요 시 spaceLikes 매핑
                List.of()  // 필요 시 reviews 매핑
        );
    }

    public static UserMeResponse.UserSpacesResponse toUserSpacesResponse(List<Space> spaces) {
        return new UserMeResponse.UserSpacesResponse(
                spaces.stream().map(UserMeConverter::toSpaceDto).collect(Collectors.toList())
        );
    }

    public static UserMeResponse.UserExhibitionsResponse toUserExhibitionsResponse(List<Exhibition> exhibitions) {
        return new UserMeResponse.UserExhibitionsResponse(
                exhibitions.stream().map(UserMeConverter::toExhibitionDto).collect(Collectors.toList())
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
