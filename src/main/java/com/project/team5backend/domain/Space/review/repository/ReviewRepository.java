package com.project.team5backend.domain.space.review.repository;


import com.project.team5backend.domain.space.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySpaceId(Long spaceId);
}