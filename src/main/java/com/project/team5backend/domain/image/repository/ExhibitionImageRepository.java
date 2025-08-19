package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.ExhibitionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExhibitionImageRepository extends JpaRepository<ExhibitionImage, Long> {

    @Query("select ei.fileKey from ExhibitionImage ei where ei.exhibition.id =:exhibitionId")
    List<String> findFileKeysByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    List<ExhibitionImage> findByExhibitionId(long exhibitionId);
}
