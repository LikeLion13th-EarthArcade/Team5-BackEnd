package com.project.team5backend.domain.space.reservation.dto.request;

import java.time.LocalDate;

public class ReservationRequest {
    public record ReservationSpace(
            Long spaceId,
            String requesterName, // 예약자 이름
            String requesterEmail, // 예약자 이메일
            String requesterPhone, // 예약자 전화번호
            String requestMessage, // 요청사항
            LocalDate startDate, // 이용 시작날짜
            LocalDate endDate // 이용 마감날짜
    ) {}
    public record CancelRequest(
            String reason // 선택한 사유 또는 기타 입력
    ) {}

}
