package com.project.team5backend.domain.space.space.repository;


import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpaceSize;
import com.project.team5backend.domain.space.space.entity.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SpaceRepositoryCustom {
    Page<Space> findSpacesWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            String district,
            SpaceSize size,
            SpaceType type,
            SpaceMood mood,
            Pageable pageable
    );
}
