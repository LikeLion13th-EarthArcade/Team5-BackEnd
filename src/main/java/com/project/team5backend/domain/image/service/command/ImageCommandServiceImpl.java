package com.project.team5backend.domain.image.service.command;

import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.dto.request.ImageReqDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageCommandServiceImpl implements ImageCommandService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final RedisImageTracker redisImageTracker;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-duration:15}")
    private long presignedUrlDurationMinutes;

    @Value(("${aws.s3.region}"))
    private String region;

    /**
     * Presigned URL 발급 , 프로필 이미지 전용
     * @param "fileExtension" 파일 확장자 (예: jpg, png)
     * @param "contentType" MIME 타입 (예: image/jpeg)
     * @return Presigned URL과 파일 키
     */
    @Override
    public ImageResDTO.PresignedUrlResDTO generatePresignedUrl(String email, ImageReqDTO.PresignedUrlReqDTO presignedUrlReqDTO) {
        if (redisImageTracker.getImageCountByEmail(email) >= 5) {
            throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);
        }
        return generateSinglePresignedUrl(email, presignedUrlReqDTO);
    }

    /**
     * 단일 Presigned URL 생성 공통 메서드
     * @param email 사용자 이메일
     * @param presignedUrlReqDTO 요청 DTO
     * @return Presigned URL 응답 DTO
     */
    private ImageResDTO.PresignedUrlResDTO generateSinglePresignedUrl(String email, ImageReqDTO.PresignedUrlReqDTO presignedUrlReqDTO) {
        validateFileExtension(presignedUrlReqDTO.fileExtension());
        validateContentType(presignedUrlReqDTO.contentType());

        String fileKey = generateFileKey(presignedUrlReqDTO.fileExtension());

        try {
            PutObjectRequest putObjectRequest = ImageConverter.toPutObjectRequest(bucketName, fileKey, presignedUrlReqDTO.contentType());

            PutObjectPresignRequest presignRequest = ImageConverter.toPutObjectPresignRequest(Duration.ofMinutes(presignedUrlDurationMinutes), putObjectRequest);
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            // Redis에 추적 정보 저장
            redisImageTracker.save(email, fileKey);

            return ImageConverter.toPresignedUrlResDTO(presignedUrl,fileKey);

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }

    /**
     * 이미지 삭제
     * @param fileKey 파일 키만 삭제
     */
    @Override
    public ImageResDTO.DeleteImageResDTO delete(String email, String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_KEY_MISSING);
        }

        try {
            // S3에서 파일 삭제
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);

            // Redis에서도 추적 정보 제거
            redisImageTracker.remove(email, fileKey);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DELETE_FAIL);
        }
        return ImageConverter.toImageDeleteResDTO(fileKey);
    }
    @Override
    public void moveToTrashPrefix(List<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) {
            log.info("이동할 파일이 없습니다.");
            return;
        }

        List<String> failedKeys = new ArrayList<>();

        for (String src : fileKeys) {
            try {
                String dst = "trash/" + src; // 원래 경로 보존
                // 1. 복사
                CopyObjectRequest copyReq = CopyObjectRequest.builder()
                        .sourceBucket(bucketName)
                        .sourceKey(src)
                        .destinationBucket(bucketName)
                        .destinationKey(dst)
                        .build();
                s3Client.copyObject(copyReq);

                // 2. 원본 삭제
                DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(src)
                        .build();
                s3Client.deleteObject(deleteReq);

                log.info("파일 휴지통 이동 완료: {} -> {}", src, dst);

            } catch (Exception e) {
                log.error("파일 휴지통 이동 실패: {}", src, e);
                failedKeys.add(src);
            }
        }
        if (!failedKeys.isEmpty()) {
            log.warn("휴지통 이동 실패한 파일들: {}", failedKeys);
            // 필요시 재시도 로직이나 알림 추가
        }
    }
    @Override
    public void clearTrackingByEmail(String email) {
        List<String> fileKeys = redisImageTracker.getOrderedFileKeysByEmail(email);

        try {
            // S3에서 파일들 삭제
            for (String fileKey : fileKeys) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build();
                s3Client.deleteObject(deleteRequest);
            }

            // Redis에서 사용자 이미지 모두 삭제 (한 번에)
            redisImageTracker.clearUserImages(email);

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DELETE_FAIL);
        }
    }

    /**
     * 파일 존재 여부 확인
     */
    private boolean isFileExists(String fileKey) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.headObject(headRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.REDIS_KEY_FETCH_FAIL);
        }
    }

    /**
     * 파일 키 생성
     */
    private String generateFileKey(String fileExtension) {
        String uuid = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("images/%s_%s.%s", timestamp, uuid, fileExtension);
    }

    /**
     * 파일 확장자 검증
     */
    private void validateFileExtension(String fileExtension) {
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_INVALID_EXTENSION);
        }

        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
        String lowerExtension = fileExtension.toLowerCase();

        for (String allowed : allowedExtensions) {
            if (allowed.equals(lowerExtension)) {
                return;
            }
        }

        throw new ImageException(ImageErrorCode.IMAGE_INVALID_CONTENT_TYPE);
    }

    /**
     * Content Type 검증
     */
    private void validateContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_INVALID_CONTENT_TYPE);
        }

        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

        for (String allowed : allowedTypes) {
            if (allowed.equals(contentType)) {
                return;
            }
        }

        throw new ImageException(ImageErrorCode.IMAGE_INVALID_CONTENT_TYPE);
    }
}