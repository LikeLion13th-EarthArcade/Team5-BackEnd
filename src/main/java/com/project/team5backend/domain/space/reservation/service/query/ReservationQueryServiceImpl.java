package com.project.team5backend.domain.space.reservation.service.query;

import com.project.team5backend.domain.space.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.space.reservation.dto.response.ReservationResponse;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservationConverter reservationConverter;
    private final UserRepository userRepository; // ✨ UserRepository를 추가합니다.


    // 예약 목록 조회
    @Override
    public List<ReservationResponse.ListResponse> getReservations(Long spaceOwnerId) {
        return reservationRepository.findBySpaceUser_Id(spaceOwnerId).stream()
                .map(reservationConverter::toListResponse)
                .toList();
    }

    // 예약 상세 조회
    @Override
    public ReservationResponse.DetailResponse getReservationDetail(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservationConverter::toDetailResponse)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }
}


