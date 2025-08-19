package com.project.team5backend.domain.space.reservation.service.query;

import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationQueryService {
    List<ReservationResponse.ListResponse> getReservations(Long spaceOwnerId);
    ReservationResponse.DetailResponse getReservationDetail(Long reservationId);
}