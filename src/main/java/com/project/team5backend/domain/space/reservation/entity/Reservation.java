package com.project.team5backend.domain.space.reservation.entity;


import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(length = 500)
    private String cancelReason; // 취소 사유(기타)

    public void cancel(ReservationStatus status, String reason) {
        this.status = status; // CANCELED
        this.cancelReason = reason;
    }


    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELED

    public void status(ReservationStatus reservationStatus) {
        this.status = status;
    }
}

