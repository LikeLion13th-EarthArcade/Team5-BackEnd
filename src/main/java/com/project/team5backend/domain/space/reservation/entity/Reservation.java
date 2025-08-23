package com.project.team5backend.domain.space.reservation.entity;


import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 예약자

    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    private String requestMessage;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime reservedAt; // 예약 일시


    @Column(length = 500)
    private String cancelReason; // 취소 사유

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELED

    // 예약 확정
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    // 예약 취소
    public void cancel(String reason) {
        this.status = ReservationStatus.CANCELED;
        this.cancelReason = reason;
    }

    // 생성 시 PENDING으로 초기화
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ReservationStatus.PENDING;
        }
        if (this.reservedAt == null) {
            this.reservedAt = LocalDateTime.now();
        }
    }
}

