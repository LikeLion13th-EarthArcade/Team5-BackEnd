package com.project.team5backend.domain.Space.repository;

import com.project.team5backend.domain.Space.entity.SpaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    Optional<SpaceLike> findBySpaceIdAndUserId(Long spaceId, Long userId);
    void deleteBySpaceIdAndUserId(Long spaceId, Long userId);
}

