package com.project.team5backend.domain.space.review.service.command;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ReviewConverter reviewConverter;

    private final RedisImageTracker redisImageTracker;
    private final ReviewImageRepository reviewImageRepository;




    @Override
    public void createReview(Long spaceId, Long userId, ReviewRequest.CreateRe request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<String> fileKeys = redisImageTracker.getOrderedFileKeysByEmail(user.getEmail());
        String mainImageKey = fileKeys.isEmpty() ? null : fileKeys.get(0);

        Review review = reviewConverter.toReview(request, space, user, mainImageKey);
        Review savedReview = reviewRepository.save(review);

        for (String fileKey : fileKeys) {
            reviewImageRepository.save(ImageConverter.toEntityReviewImage(savedReview, fileKey));
            redisImageTracker.remove(user.getEmail(), fileKey);
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