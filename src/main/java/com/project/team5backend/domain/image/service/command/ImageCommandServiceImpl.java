package com.project.team5backend.domain.image.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageCommandServiceImpl implements ImageCommandService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

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
}