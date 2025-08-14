package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExhibitionRepositoryCustom {
    Page<Exhibition> findExhibitionsWithFilters(Category category, String district, Mood mood, LocalDate localDate, ExhibitionSort sort, Pageable pageable);
}
