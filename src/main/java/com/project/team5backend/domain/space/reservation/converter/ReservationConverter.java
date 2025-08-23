package com.project.team5backend.domain.space.reservation.converter;

import com.project.team5backend.domain.space.reservation.dto.request.ReservationRequest;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class ReservationConverter {
    private final UserRepository userRepository;

    public Reservation toEntity(ReservationRequest.ReservationSpace request, Space space,User user) {
        return Reservation.builder()
                .space(space)
                .user(user)
                .requesterName(request.requesterName())
                .requesterEmail(request.requesterEmail())
                .requesterPhone(request.requesterPhone())
                .requestMessage(request.requestMessage())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .reservedAt(LocalDateTime.now()) // 예약 시각 자동 세팅
                .status(ReservationStatus.PENDING)
                .build();
    }

    public ReservationResponse.ListResponse toListResponse(Reservation reservation) {
        return new ReservationResponse.ListResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getRequesterName(),
                reservation.getSpace().getName(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );
    }

    public ReservationResponse.DetailResponse toDetailResponse(Reservation reservation) {
        return new ReservationResponse.DetailResponse(
                reservation.getId(),
                reservation.getSpace().getId(),
                reservation.getSpace().getName(),
                reservation.getRequesterName(),
                reservation.getRequesterEmail(),
                reservation.getRequesterPhone(),
                reservation.getRequestMessage(),
                reservation.getStatus().name(),
                reservation.getReservedAt() // 예약 시각 포함

        );
    }
}

