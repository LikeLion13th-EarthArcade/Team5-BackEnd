package com.project.team5backend.domain.exhibition.review.repository;

import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReveiw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionReviewRepository extends JpaRepository<ExhibitionReveiw, Long> {
}
