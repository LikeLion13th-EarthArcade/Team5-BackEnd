package com.project.team5backend.domain.Space.repository;

import com.project.team5backend.domain.Space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByStatus(Space.Status status);
}