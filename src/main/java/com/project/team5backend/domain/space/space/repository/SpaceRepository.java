package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.space.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByStatus(Space.Status status);
}