package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, ExhibitionRepositoryCustom {
    // 지금 뜨는 전시회
    @Query("""
        select e from Exhibition e
        where e.isDeleted = false
        and e.startDate <=:current
        and e.endDate >=:current
        order by e.reviewCount desc , e.createdAt desc
   """)
    List<Exhibition> findHotNowExhibition(@Param("current") LocalDate current, Pageable pageable);

    // 리뷰 평균/카운트 갱신
    @Modifying
    @Query("""
        update Exhibition e
        set e.reviewCount = e.reviewCount + 1,
            e.ratingAvg = ((e.ratingAvg * (e.reviewCount - 1)) + :rating) / (e.reviewCount)
        where e.id =:exhibitionId
        """)
    void applyReviewCreated(@Param("exhibitionId") Long exhibitionId, @Param("rating")  double rating);

    @Modifying
    @Query("""
        update Exhibition e
        set e.reviewCount = e.reviewCount - 1,
            e.ratingAvg   = case
                              when e.reviewCount <= 0
                                then 0
                              else ((e.ratingAvg * (e.reviewCount + 1)) - :rating) / e.reviewCount
                             end
        where e.id =:exhibitionId and e.reviewCount > 0
        """)
    void applyReviewDeleted(@Param("exhibitionId") Long exhibitionId, @Param("rating")  double rating);

}
