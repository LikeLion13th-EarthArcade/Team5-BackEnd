package com.project.team5backend.domain.space.reservation.repository;


import com.project.team5backend.domain.space.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 1. 내가 예약한 공간
    List<Reservation> findByUser_Id(Long userId);

    // 2. 내가 등록한 공간에 들어온 예약
    List<Reservation> findBySpaceUser_Id(Long userId);

}
