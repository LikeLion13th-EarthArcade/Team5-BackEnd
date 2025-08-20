package com.project.team5backend.domain.user.me.converter;


import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    public static UserResponse.Activity toActivityDto(ActivityEntity entity) {
        return new UserResponse.Activity(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getCreatedAt()
        );
    }

    public static UserResponse.Space toSpaceDto(SpaceEntity entity) {
        return new UserResponse.Space(
                entity.getId(),
                entity.getName(),
                entity.getAddress()
        );
    }

    public static UserResponse.Exhibition toExhibitionDto(ExhibitionEntity entity) {
        return new UserResponse.Exhibition(
                entity.getId(),
                entity.getTitle(),
                entity.getStatus()
        );
    }

    public static UserResponse.Reservation toReservationDto(ReservationEntity entity) {
        return new UserResponse.Reservation(
                entity.getId(),
                entity.getSpace().getName(),
                entity.getStatus(),
                entity.getReservedAt()
        );
    }

    public static UserResponse toUserResponse(
            List<ActivityEntity> activities,
            List<SpaceEntity> spaces,
            List<ExhibitionEntity> exhibitions,
            List<ReservationEntity> reservations
    ) {
        return new UserResponse(
                activities.stream().map(UserConverter::toActivityDto).collect(Collectors.toList()),
                spaces.stream().map(UserConverter::toSpaceDto).collect(Collectors.toList()),
                exhibitions.stream().map(UserConverter::toExhibitionDto).collect(Collectors.toList()),
                reservations.stream().map(UserConverter::toReservationDto).collect(Collectors.toList())
        );
    }
}
