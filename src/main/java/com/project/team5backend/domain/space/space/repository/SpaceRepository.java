package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>,SpaceRepositoryCustom {
    List<Space> findByStatus(Space.Status status);
    List<Space> findByUserId(Long userId);

}