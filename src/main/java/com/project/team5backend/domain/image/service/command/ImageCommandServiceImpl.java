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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return generateSinglePresignedUrl(email, presignedUrlReqDTO);
    }

    /**
     * Presigned URL 발급 , 게시글 이미지 전용
     * @param "fileExtension" 파일 확장자 (예: jpg, png)
     * @param "contentType" MIME 타입 (예: image/jpeg)
     * @return Presigned URL과 파일 키
     */
    @Override
    public ImageResDTO.PresignedUrlListResDTO generatePresignedUrlList(String email, ImageReqDTO.PresignedUrlListReqDTO presignedUrlListReqDTO) {
        if (presignedUrlListReqDTO.images().size() > 5) {
            throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);
        }

        List<ImageResDTO.PresignedUrlResDTO> resDTOList = presignedUrlListReqDTO.images().stream()
                .map(presignedUrlReqDTO -> generateSinglePresignedUrl(email, presignedUrlReqDTO))
                .collect(Collectors.toList());

        return ImageConverter.toPresignedUrlListResDTO(resDTOList);
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
     * 이미지 사용 확정 (commit)
     */
    @Override
    public String commit(String email, String fileKey) {

        if (fileKey == null || fileKey.trim().isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_KEY_MISSING);
        }

        try {
            // S3에 파일이 실제로 업로드되었는지 확인
            if (!isFileExists(fileKey)) {
                log.warn("fileKey를 찾을 수 없습니다: {}", fileKey);
                throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
            }

            // Redis에서 추적 정보 제거 (더 이상 정리 대상이 아님)
            redisImageTracker.remove(email, fileKey);

            log.info("이미지가 성공적으로 저장되었습니다: {}", fileKey);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_COMMIT_FAIL);
        }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileKey;
    }

    /**
     * 이미지 실제 삭제
     * @param fileKey 파일 키
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