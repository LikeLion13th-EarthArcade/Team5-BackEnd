package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.space.space.entity.SpaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    Optional<SpaceLike> findBySpaceIdAndUserId(Long spaceId, Long userId);
    List<SpaceLike> findByUser_Id(Long userId);
}


