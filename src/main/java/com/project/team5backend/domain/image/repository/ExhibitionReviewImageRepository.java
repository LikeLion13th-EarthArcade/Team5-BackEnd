package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionReviewImageRepository extends JpaRepository<ExhibitionReviewImage, Long> {
}
