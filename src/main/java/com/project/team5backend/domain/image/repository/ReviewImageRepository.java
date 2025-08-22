package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @Query("select ri.fileKey from ReviewImage ri where ri.review.id = :reviewId")
    List<String> findFileKeysByReviewId(@Param("reviewId") Long reviewId);

    List<ReviewImage> findByReviewId(long reviewId);
}