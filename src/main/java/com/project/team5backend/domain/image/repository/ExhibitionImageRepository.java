package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.ExhibitionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionImageRepository extends JpaRepository<ExhibitionImage, Long> {
}
