package com.project.team5backend.domain.recommendation.repository;

import com.project.team5backend.domain.recommendation.entity.ExhibitionEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExhibitionEmbeddingRepository extends JpaRepository<ExhibitionEmbedding, Long> {
    List<ExhibitionEmbedding> findByExhibitionIdIn(Collection<Long> ids);
}
