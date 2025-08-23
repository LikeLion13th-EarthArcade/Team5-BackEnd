package com.project.team5backend.domain.space.reservation.converter;

import com.project.team5backend.domain.space.reservation.dto.request.ReservationRequest;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.space.space.entity.Space;
import org.springframework.stereotype.Component;


@Component
public class ReservationConverter {

    public Reservation toEntity(ReservationRequest.ReservationSpace request, Space space) {
        return Reservation.builder()
                .space(space)
                .requesterName(request.requesterName())
                .requesterEmail(request.requesterEmail())
                .requesterPhone(request.requesterPhone())
                .requestMessage(request.requestMessage())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(ReservationStatus.PENDING)
                .build();
    }

    public ReservationResponse.ListResponse toListResponse(Reservation reservation) {
        return new ReservationResponse.ListResponse(
                reservation.getId(),
                reservation.getStatus().name(),   // Enum → String
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
                reservation.getStatus().name()    // Enum → String
        );
    }
}
