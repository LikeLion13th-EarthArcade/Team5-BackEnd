package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByStatus(Space.Status status);
}