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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisImageTracker {

    private final RedisTemplate<String, String> imageRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisImageTracker(
            @Qualifier("imageRedisTemplate") RedisTemplate<String, String> imageRedisTemplate,
            ObjectMapper objectMapper
    ) {
        this.imageRedisTemplate = imageRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String REDIS_ZSET_KEY_PREFIX = "images:ordered:";
    private static final String REDIS_DETAIL_KEY_PREFIX = "images:detail:";
    private static final long TRACKING_DURATION_MINUTES = 30;

    /**
     * Redis ZSet에 이미지 순서와 함께 저장
     */
    public void save(String email, String fileKey) {
        try {
            // 1. ZSet에 순서 저장 (score는 현재 시간)
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            double score = System.currentTimeMillis();
            imageRedisTemplate.opsForZSet().add(zsetKey, fileKey, score);
            imageRedisTemplate.expire(zsetKey, TRACKING_DURATION_MINUTES, TimeUnit.MINUTES);

            // 2. 상세 정보도 별도 저장 (기존 방식 유지)
            String detailKey = REDIS_DETAIL_KEY_PREFIX + email + ":" + fileKey;
            ImageInternelDTO.ImageTrackingResDTO imageTrackingResDTO = ImageConverter.toImageTrackingResDTO(email, fileKey);
            String json = objectMapper.writeValueAsString(imageTrackingResDTO);
            imageRedisTemplate.opsForValue().set(detailKey, json, TRACKING_DURATION_MINUTES, TimeUnit.MINUTES);

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_SAVE_FAIL);
        }
    }

    /**
     * Redis에서 이미지 추적 정보 제거
     */
    public void remove(String email, String fileKey) {
        try {
            // 1. ZSet에서 제거
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            imageRedisTemplate.opsForZSet().remove(zsetKey, fileKey);

            // 2. 상세 정보도 제거
            String detailKey = REDIS_DETAIL_KEY_PREFIX + email + ":" + fileKey;
            imageRedisTemplate.delete(detailKey);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_REMOVE_FAIL);
        }
    }

    /**
     * email에 맞는 이미지를 순서대로 조회 (ZSet 사용)
     */
    public List<String> getOrderedFileKeysByEmail(String email) {
        try {
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            Set<String> orderedSet = imageRedisTemplate.opsForZSet().range(zsetKey, 0, -1);
            return new ArrayList<>(orderedSet); // 순서 보장된 List 반환
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_KEY_FETCH_FAIL);
        }
    }

    /**
     * 기존 호환성을 위한 메서드 (Set 반환)
     */
    @Deprecated
    public Set<String> getTrackedFileKeysByEmail(String email) {
        List<String> orderedList = getOrderedFileKeysByEmail(email);
        return new LinkedHashSet<>(orderedList); // 순서 유지하는 Set으로 변환
    }

    /**
     * 해당 유저의 모든 임시 이미지 삭제 (쓰레기 데이터 정리용)
     */
    public void clearUserImages(String email) {
        try {
            // 1. ZSet 삭제
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            imageRedisTemplate.delete(zsetKey);

            // 2. 상세 정보들도 삭제
            Set<String> detailKeys = imageRedisTemplate.keys(REDIS_DETAIL_KEY_PREFIX + email + ":*");
            if (detailKeys != null && !detailKeys.isEmpty()) {
                imageRedisTemplate.delete(detailKeys);
            }

            log.info("사용자 {}의 임시 이미지 모두 삭제 완료", email);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_REMOVE_FAIL);
        }
    }

    /**
     * 특정 시간 이전의 추적 정보 조회 (기존 방식 유지)
     */
    public Set<ImageInternelDTO.ImageTrackingResDTO> getExpiredImageEntries(LocalDateTime expiredBefore) {
        Set<String> detailKeys = imageRedisTemplate.keys(REDIS_DETAIL_KEY_PREFIX + "*");
        Set<ImageInternelDTO.ImageTrackingResDTO> expiredEntries = new HashSet<>();

        if (detailKeys == null) {
            return expiredEntries;
        }

        for (String detailKey : detailKeys) {
            try {
                String json = imageRedisTemplate.opsForValue().get(detailKey);
                if (json != null) {
                    ImageInternelDTO.ImageTrackingResDTO dto = objectMapper.readValue(json, ImageInternelDTO.ImageTrackingResDTO.class);
                    if (dto.createAt().isBefore(expiredBefore)) {
                        expiredEntries.add(dto);
                    }
                }
            } catch (Exception e) {
                log.warn("만료 여부 확인 실패: {}", detailKey, e);
            }
        }

        return expiredEntries;
    }

    /**
     * fileKey의 주인 확인
     */
    public boolean isOwnedByUser(String email, String fileKey) {
        String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
        Double score = imageRedisTemplate.opsForZSet().score(zsetKey, fileKey);
        return score != null; // score가 있으면 해당 유저 소유
    }

    /**
     * 이미지 개수 조회
     */
    public long getImageCountByEmail(String email) {
        try {
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            Long count = imageRedisTemplate.opsForZSet().count(zsetKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_KEY_FETCH_FAIL);
        }
    }

    /**
     * 디버깅용: ZSet 내용을 score와 함께 조회
     */
    public Map<String, Double> getImagesWithScores(String email) {
        try {
            String zsetKey = REDIS_ZSET_KEY_PREFIX + email;
            Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<String>> tuples =
                    imageRedisTemplate.opsForZSet().rangeWithScores(zsetKey, 0, -1);

            Map<String, Double> result = new LinkedHashMap<>();
            if (tuples != null) {
                for (org.springframework.data.redis.core.ZSetOperations.TypedTuple<String> tuple : tuples) {
                    result.put(tuple.getValue(), tuple.getScore());
                }
            }
            return result;
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_KEY_FETCH_FAIL);
        }
    }
}