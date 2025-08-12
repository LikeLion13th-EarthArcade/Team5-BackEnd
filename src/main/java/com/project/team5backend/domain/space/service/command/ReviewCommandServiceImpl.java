package com.project.team5backend.domain.space.service.command;



import com.project.team5backend.domain.space.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.entity.Review;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.repository.ReviewRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createReview(Long spaceId, Long userId, ReviewRequest.Create request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImageUrls(request.getImages());
        review.setCreatedAt(LocalDateTime.now());
        review.setSpace(space);
        review.setUser(user);

        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of this review");
        }

        reviewRepository.delete(review);
    }
}