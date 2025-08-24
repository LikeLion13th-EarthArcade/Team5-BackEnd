package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.ExhibitionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

    Boolean existsByUserIdAndExhibitionId(@Param("userId") Long userId, @Param("exhibitionId") Long exhibitionId);

    @Modifying
    @Query("delete from ExhibitionLike el where el.user.id =:userId and el.exhibition.id =:exhibitionId")
    void deleteByUserIdAndExhibitionId(@Param("userId") Long userId,@Param("exhibitionId") Long exhibitionId);

    @Modifying
    @Query("delete from ExhibitionLike ei where ei.exhibition.id =:exhibitionId")
    void deleteByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    @Query("select el.exhibition.id from ExhibitionLike el " +
            "where el.user.id = :userId and el.exhibition.id in :ids")
    List<Long> findLikedExhibitionIds(@Param("userId") Long userId,
                                      @Param("ids") Collection<Long> ids);
    List<ExhibitionLike> findByUser_Id(Long userId);
}
