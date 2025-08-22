package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.SpaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SpaceImageRepository extends JpaRepository<SpaceImage, Long> {

    @Query("select si.fileKey from SpaceImage si where si.space.id = :spaceId")
    List<String> findFileKeysBySpaceId(@Param("spaceId") Long spaceId);

    List<SpaceImage> findBySpaceId(long spaceId);
}