package com.project.team5backend.domain.recommendation.scheduler;

import com.project.team5backend.domain.recommendation.repository.ExhibitionInteractLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class InteractLogRetentionJob {

    private final ExhibitionInteractLogRepository logRepo;

    // 매일 새벽 4시 5분에 180일 지난 로그 일괄 삭제
    @Scheduled(cron = "0 5 4 * * *", zone = "Asia/Seoul")
    public void purgeOldLogs() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(180);
        int deleted = logRepo.purgeBefore(threshold);
        log.info("[보존정책] {}일 이전의 상호작용 로그 {}건 삭제 완료", threshold, deleted);
    }
}