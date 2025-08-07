package com.project.team5backend.domain.image.converter;

import com.project.team5backend.domain.image.dto.internel.ImageInternelDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageConverter {
    public static ImageResDTO.PresignedUrlResDTO toPresignedUrlResDTO(String presignedUrl, String fileKey) {
        return ImageResDTO.PresignedUrlResDTO.builder()
                .presignedUrl(presignedUrl)
                .fileKey(fileKey)
                .build();
    }
    public static ImageResDTO.PresignedUrlListResDTO toPresignedUrlListResDTO(List<ImageResDTO.PresignedUrlResDTO> urls) {
        return new ImageResDTO.PresignedUrlListResDTO(urls);
    }

    public static ImageInternelDTO.ImageTrackingResDTO toImageTrackingResDTO(String email, String fileKey) {
        return ImageInternelDTO.ImageTrackingResDTO.builder()
                .fileKey(fileKey)
                .email(email)
                .createAt(LocalDateTime.now())
                .build();
    }

    public static PutObjectRequest toPutObjectRequest(String bucketName, String fileKey, String contentType) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();
    }

    public static PutObjectPresignRequest toPutObjectPresignRequest(Duration presignedUrlDurationMinutes, PutObjectRequest putObjectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(presignedUrlDurationMinutes)
                .putObjectRequest(putObjectRequest)
                .build();
    }

    public static ImageResDTO.DeleteImageResDTO toImageDeleteResDTO(String fileKey) {
        return ImageResDTO.DeleteImageResDTO.builder()
                .fileKey(fileKey)
                .isDeleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
    }

}
