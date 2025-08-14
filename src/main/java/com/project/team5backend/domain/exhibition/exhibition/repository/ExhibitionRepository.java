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

    // 다가오는, 지금뜨는 전시회
    @Query("""
        select e from Exhibition e
        where e.isDeleted = false
        and e.startDate > :current
        order by e.likeCount desc, e.createdAt desc
        """)
    List<Exhibition> findUpcomingPopularExhibition(@Param("current") LocalDate current);

    // 지금 뜨는 지역구 전시회
    @Query(value = """
    WITH ranked AS (
      SELECT e.*,
            ROW_NUMBER() OVER (
             PARTITION BY e.district
             ORDER BY e.rating_count DESC, e.updated_at DESC, e.id DESC
           ) rn
      FROM exhibition e
     WHERE e.is_deleted = false
       AND e.start_date <= :currentDate
       AND e.end_date   >= :currentDate
    )
    SELECT * FROM ranked
    WHERE rn = 1
    ORDER BY rating_count DESC, updated_at DESC, id DESC
    LIMIT 4
    """, nativeQuery = true)
    List<Exhibition> findTopByDistrict(@Param("currentDate") LocalDate currentDate);

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
