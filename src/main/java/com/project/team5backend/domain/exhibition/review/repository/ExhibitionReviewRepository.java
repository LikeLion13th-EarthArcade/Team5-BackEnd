package com.project.team5backend.domain.exhibition.review.repository;

import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionReviewRepository extends JpaRepository<ExhibitionReview, Long> {
}
