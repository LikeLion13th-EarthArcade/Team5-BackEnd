package com.project.team5backend.domain.recommendation.repository;

import com.project.team5backend.domain.recommendation.entity.ExhibitionInteractLog; // 네가 가진 엔티티 경로 맞추기
import com.project.team5backend.domain.recommendation.dto.KeyScoreRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExhibitionInteractLogRepository extends JpaRepository<ExhibitionInteractLog, Long> {

    @Query(value = """
        SELECT COUNT(*) FROM exhibition_interact_log
        WHERE user_id = :userId AND created_at >= :since
        """, nativeQuery = true)
    long countSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query(value = """
        SELECT e.category AS keyName,
               CAST(SUM(CASE l.action_type WHEN 'LIKE' THEN 1.6 WHEN 'CLICK' THEN 1.0 ELSE 1.0 END) AS DOUBLE) AS score
        FROM exhibition_interact_log l
        JOIN exhibition e ON e.id = l.exhibition_id
        WHERE l.user_id = :userId AND l.created_at >= :since
        GROUP BY e.category
        ORDER BY
            SUM(CASE l.action_type WHEN 'LIKE' THEN 1.6 WHEN 'CLICK' THEN 1.0 ELSE 1.0 END) DESC,                 -- 1순위: 가중치 합
            MAX(l.created_at) DESC,     -- 2순위: 가장 최근 상호작용
            COUNT(*) DESC              -- 3순위: 상호작용 개수
        LIMIT 1
        """, nativeQuery = true)
    KeyScoreRow topCategory(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query(value = """
        SELECT e.mood AS keyName,
               CAST(SUM(CASE l.action_type WHEN 'LIKE' THEN 1.6 WHEN 'CLICK' THEN 1.0 ELSE 1.0 END) AS DOUBLE) AS score
        FROM exhibition_interact_log l
        JOIN exhibition e ON e.id = l.exhibition_id
        WHERE l.user_id = :userId AND l.created_at >= :since
        GROUP BY e.mood
        ORDER BY
            SUM(CASE l.action_type WHEN 'LIKE' THEN 1.6 WHEN 'CLICK' THEN 1.0 ELSE 1.0 END) DESC,                 -- 1순위: 가중치 합
            MAX(l.created_at) DESC,     -- 2순위: 가장 최근 상호작용
            COUNT(*) DESC              -- 3순위: 상호작용 개수
        LIMIT 1
        """, nativeQuery = true)
    KeyScoreRow topMood(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query(value = """
        SELECT exhibition_id
        FROM exhibition_interact_log
        WHERE user_id = :userId AND created_at >= :since
        ORDER BY created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Long> findRecentExhibitionIds(@Param("userId") Long userId,
                                       @Param("since") LocalDateTime since,
                                       @Param("limit") int limit);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ExhibitionInteractLog e WHERE e.createdAt < :threshold")
    int purgeBefore(@Param("threshold") LocalDateTime threshold);

    // 최근 5분 내 동일 유저·전시·행동이 있었는지
    @Query(value = """
  SELECT COUNT(*)
  FROM exhibition_interact_log
  WHERE user_id = :userId
    AND exhibition_id = :exhibitionId
    AND action_type = :actionType
    AND created_at >= :since
  """, nativeQuery = true)
    long countRecent(@Param("userId") Long userId,
                     @Param("exhibitionId") Long exhibitionId,
                     @Param("actionType") String actionType,   // CLICK/LIKE
                     @Param("since") java.time.LocalDateTime since);
}