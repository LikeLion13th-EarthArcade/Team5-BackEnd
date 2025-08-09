package com.project.team5backend.domain.exhibition.review.repository;

import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionReviewRepository extends JpaRepository<ExhibitionReview, Long> {
    @Modifying
    @Query("update ExhibitionReview er set er.isDeleted = true where er.exhibition.id =:exhibitionId")
    void softDeleteByExhibitionId(@Param("exhibitionId") Long exhibitionId);
}
