package com.project.team5backend.domain.image.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.dto.internel.ImageInternelDTO;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisImageTracker {

    private final RedisTemplate<String, String> imageRedisTemplate;
    private final ObjectMapper objectMapper;

    // 생성자에서 @Qualifier로 imageRedisTemplate 주입
    public RedisImageTracker(
            @Qualifier("imageRedisTemplate") RedisTemplate<String, String> imageRedisTemplate,
            ObjectMapper objectMapper
    ) {
        this.imageRedisTemplate = imageRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String REDIS_KEY_PREFIX = "images:";
    private static final long TRACKING_DURATION_HOURS = 24; // 24시간 추적

    /**
     * Redis에 이미지 추적 정보 저장
     */
    public void save(String email, String fileKey) {
        try {
            String redisKey = REDIS_KEY_PREFIX + email + ":" + fileKey;

            ImageInternelDTO.ImageTrackingResDTO imageTrackingResDTO = ImageConverter.toImageTrackingResDTO(email, fileKey);
            String json = objectMapper.writeValueAsString(imageTrackingResDTO);
            imageRedisTemplate.opsForValue().set(redisKey, json, TRACKING_DURATION_HOURS, TimeUnit.HOURS);

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_SAVE_FAIL);
        }
    }

    /**
     * Redis에서 이미지 추적 정보 제거
     */
    public void remove(String email, String fileKey) {
        try {
            String redisKey = REDIS_KEY_PREFIX + email + ":" + fileKey;
            imageRedisTemplate.delete(redisKey);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_REMOVE_FAIL);
        }
    }

    /**
     * 모든 추적 중인 이미지 키 조회
     */
    public Set<String> getAllTrackedFileKeys() {
        try {
            Set<String> redisKeys = imageRedisTemplate.keys(REDIS_KEY_PREFIX + "*");
            return redisKeys.stream()
                    .map(key -> key.substring(REDIS_KEY_PREFIX.length()))
                    .collect(java.util.stream.Collectors.toSet());

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_KEY_FETCH_FAIL);
        }
    }

    /**
     * 특정 시간 이전의 추적 정보 조회
     */
    public Set<ImageInternelDTO.ImageTrackingResDTO> getExpiredImageEntries(LocalDateTime expiredBefore) {
        Set<String> redisKeys = imageRedisTemplate.keys(REDIS_KEY_PREFIX + "*");
        Set<ImageInternelDTO.ImageTrackingResDTO> expiredEntries = new HashSet<>();

        for (String redisKey : redisKeys) {
            try {
                Object raw = imageRedisTemplate.opsForValue().get(redisKey);
                ImageInternelDTO.ImageTrackingResDTO dto = objectMapper.convertValue(raw, ImageInternelDTO.ImageTrackingResDTO.class);

                if (dto.createAt().isBefore(expiredBefore)) {
                    expiredEntries.add(dto);
                }
            } catch (Exception e) {
                log.warn("만료 여부 확인 실패: {}", redisKey, e);
            }
        }

        return expiredEntries;
    }
    /**
     * fileKey의 주인 찾기
     */
    public boolean isOwnedByUser(String email, String fileKey) {
        String redisKey = REDIS_KEY_PREFIX + email + ":" + fileKey;
        return imageRedisTemplate.hasKey(redisKey);
    }
}