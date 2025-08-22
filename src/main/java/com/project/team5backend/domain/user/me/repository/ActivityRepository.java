package com.project.team5backend.domain.user.me.repository;

import com.project.team5backend.domain.user.me.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserId(Long userId);
}
