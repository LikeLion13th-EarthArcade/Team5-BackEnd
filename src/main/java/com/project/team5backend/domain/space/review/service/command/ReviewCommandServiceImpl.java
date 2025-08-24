package com.project.team5backend.domain.space.review.service.command;

import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ReviewImageRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.space.review.converter.ReviewConverter;
import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.review.repository.ReviewRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ReviewConverter reviewConverter;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Uploader s3Uploader;

    @Override
    public void createReview(Long spaceId, Long userId, ReviewRequest.CreateRe request, List<MultipartFile> images) {

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (images == null || images.isEmpty()) throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        if (images.size() > 5) throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);

        // S3 업로드 → URL 반환
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : images) {
            String url = s3Uploader.upload(file, "review"); // S3 URL
            fileUrls.add(url);
        }

        String mainImageUrl = fileUrls.get(0); // 첫 번째 이미지 대표 이미지
        Review review = reviewConverter.toReview(request, space, user, mainImageUrl);
        Review savedReview = reviewRepository.save(review);

        // 엔티티에 이미지 URL 저장
        for (String url : fileUrls) {
            reviewImageRepository.save(ImageConverter.toEntityReviewImage(savedReview, url));
        }
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of this review");
        }

        reviewRepository.delete(review);
    }
}

