package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.ExhibitionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

    Boolean existsByUserIdAndExhibitionId(@Param("userId") Long userId, @Param("exhibitionId") Long exhibitionId);

    @Modifying
    @Query("delete from ExhibitionLike el where el.user.id =:userId and el.exhibition.id =:exhibitionId")
    void deleteByUserIdAndExhibitionId(@Param("userId") Long userId,@Param("exhibitionId") Long exhibitionId);
}
