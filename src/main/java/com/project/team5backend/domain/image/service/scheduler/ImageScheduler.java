package com.project.team5backend.domain.image.service.scheduler;
import com.project.team5backend.domain.image.dto.internel.ImageInternelDTO;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageScheduler {

    private final ImageCommandService imageCommandService;
    private final RedisImageTracker redisImageTracker;

    /**
     * 매 시간마다 미사용 이미지 정리
     */
    @Scheduled(cron = "0 0 * * * *") // 매 시간 0분에 실행
    public void run() {
        log.info("이미지 fileKey 스케줄러 작동");

        try {
            // 1시간 전 이전에 생성된 추적 정보 조회
            LocalDateTime expiredBefore = LocalDateTime.now().minusHours(1);
            Set<ImageInternelDTO.ImageTrackingResDTO> expiredImages = redisImageTracker.getExpiredImageEntries(expiredBefore);

            if (expiredImages.isEmpty()) {
                log.info("정리할 FileKey가 없습니다.");
                return;
            }

            log.info("총 {}개의 만료 FileKey 정리 시도", expiredImages.size());

            int deletedCount = 0;
            int errorCount = 0;

            for (ImageInternelDTO.ImageTrackingResDTO dto : expiredImages) {
                try {
                    imageCommandService.delete(dto.email(), dto.fileKey());
                    deletedCount++;
                } catch (Exception e) {
                    log.error("만료 FileKey 삭제 실패: {}", dto.fileKey(), e);
                    errorCount++;
                }
            }

            log.info("FileKey 삭제 완료 - 삭제: {}, 에러: {}", deletedCount, errorCount);

        } catch (Exception e) {
            log.error("만료 FileKey 조회 실패", e);
            throw new ImageException(ImageErrorCode.REDIS_EXPIRED_FETCH_FAIL);

        }
    }
}

