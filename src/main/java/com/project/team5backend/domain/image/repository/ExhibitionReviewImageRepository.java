package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExhibitionReviewImageRepository extends JpaRepository<ExhibitionReviewImage, Long> {
    List<ExhibitionReviewImage> findByExhibitionReviewId(Long exhibitionReviewId);
}
