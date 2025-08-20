package com.project.team5backend.domain.image.converter;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.image.dto.internel.ImageInternelDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.entity.ReviewImage;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageConverter {
    public static ExhibitionImage toEntityExhibitionImage(Exhibition exhibition, String fileKey) {
        return ExhibitionImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .exhibition(exhibition)
                .build();
    }
    // Space
    public static SpaceImage toEntitySpaceImage(Space space, String fileKey) {
        return SpaceImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .space(space)
                .build();
    }

    // Review
    public static ReviewImage toEntityReviewImage(Review review, String fileKey) {
        return ReviewImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .review(review)
                .build();
    }
    public static List<String> toFileKeyList(List<ExhibitionImage> exhibitionImages) {
        return exhibitionImages.stream()
                .map(ExhibitionImage::getFileKey)
                .collect(Collectors.toList());

    }
    public static List<String> toFileKeyListSpace(List<SpaceImage> spaceImages) {
        return spaceImages.stream()
                .map(SpaceImage::getFileKey)
                .collect(Collectors.toList());
    }

    public static List<String> toFileKeyListReview(List<ReviewImage> reviewImages) {
        return reviewImages.stream()
                .map(ReviewImage::getFileKey)
                .collect(Collectors.toList());
    }

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
