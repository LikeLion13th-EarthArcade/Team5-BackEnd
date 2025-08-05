package com.project.team5backend.domain.exhibition.repository;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
}
