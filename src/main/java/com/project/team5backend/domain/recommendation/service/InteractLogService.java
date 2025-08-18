package com.project.team5backend.domain.recommendation.service;

import com.project.team5backend.domain.recommendation.entity.ExhibitionInteractLog;
import com.project.team5backend.domain.recommendation.model.ActionType;
import com.project.team5backend.domain.recommendation.repository.ExhibitionInteractLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractLogService {

    private final ExhibitionInteractLogRepository logRepo;
    private static final java.time.Duration DEDUP = java.time.Duration.ofMinutes(5);

    public void logClick(Long userId, Long exhibitionId) {
        var since = java.time.LocalDateTime.now().minus(DEDUP);
        boolean recent = logRepo.countRecent(userId, exhibitionId, "CLICK", since) > 0;
        if (!recent) {
            logRepo.save(ExhibitionInteractLog.builder()
                    .userId(userId)
                    .exhibitionId(exhibitionId)
                    .actionType(ActionType.CLICK) // enum
                    .build());
        }
    }

    public void logLike(Long userId, Long exhibitionId) {
        var since = java.time.LocalDateTime.now().minus(DEDUP);
        boolean recent = logRepo.countRecent(userId, exhibitionId, "LIKE", since) > 0;
        if (!recent) {
            logRepo.save(ExhibitionInteractLog.builder()
                    .userId(userId)
                    .exhibitionId(exhibitionId)
                    .actionType(ActionType.LIKE)
                    .build());
        }
    }
}