package com.project.team5backend.domain.space.reservation.service.command;


import com.project.team5backend.domain.space.reservation.dto.request.ReservationRequest;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;

public interface ReservationCommandService {
    ReservationResponse.DetailResponse createReservation(Long userId, ReservationRequest.ReservationSpace request);
    ReservationResponse.DetailResponse confirmReservation(Long reservationId, Long userId);
    ReservationResponse.DetailResponse cancelReservation(Long reservationId, Long userId, String reason);
}