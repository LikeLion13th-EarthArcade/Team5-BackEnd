package com.project.team5backend.domain.space.reservation.service.command;

import com.project.team5backend.domain.space.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.space.reservation.dto.request.ReservationRequest;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.space.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;

import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;
    private final ReservationConverter reservationConverter;
    private final UserRepository userRepository;

    // 공간 예약
    @Override
    public ReservationResponse.DetailResponse createReservation(Long userId, ReservationRequest.ReservationSpace request) {
        Space space = spaceRepository.findById(request.spaceId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공간입니다."));

        // 로그인한 사용자 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Reservation reservation = reservationConverter.toEntity(request, space, user);        reservationRepository.save(reservation);

        return reservationConverter.toDetailResponse(reservation);
    }

    // 예약 확정
    @Override
    public ReservationResponse.DetailResponse confirmReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getSpace().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("예약을 확정할 권한이 없습니다.");
        }

        reservation.confirm();
        return reservationConverter.toDetailResponse(reservation);
    }

    // 예약 취소
    @Override
    public ReservationResponse.DetailResponse cancelReservation(Long reservationId, Long userId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getSpace().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("예약을 취소할 권한이 없습니다.");
        }

        reservation.cancel(reason);
        return reservationConverter.toDetailResponse(reservation);
    }
}

