package com.project.team5backend.domain.space.reservation.dto.response;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {

    // 목록 조회
    public record ListResponse(
            Long reservationId,
            String status,        // 진행중, 완료됨, 취소됨
            String userName,
            String spaceName,
            LocalDate startDate,
            LocalDate endDate

    ) {}
    // 상세 조회
    public record DetailResponse(
            Long reservationId,
            Long spaceId,
            String spaceName,
            String requesterName,
            String requesterEmail,
            String requesterPhone,
            String requestMessage,
            String status,
            LocalDateTime reservedAt
    ) {}
}


