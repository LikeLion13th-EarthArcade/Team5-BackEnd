package com.project.team5backend.domain.user.me.service.query;


import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.ExhibitionLike;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.review.repository.ReviewRepository;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.me.converter.UserMeConverter;
import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.entity.Activity;
import com.project.team5backend.domain.user.me.repository.ActivityRepository;
import com.project.team5backend.domain.space.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMeQueryServiceImpl implements UserMeQueryService {

    private final ActivityRepository activityRepository;
    private final SpaceRepository spaceRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReservationRepository reservationRepository;

    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;

    @Override
    public UserMeResponse.UserActivityResponse getMyActivity(Long userId) {
        List<ExhibitionLike> exhibitionLikes = exhibitionLikeRepository.findByUser_Id(userId);
        List<SpaceLike> spaceLikes = spaceLikeRepository.findByUser_Id(userId);
        // 공간 리뷰
        List<Review> spaceReviews = reviewRepository.findByUser_Id(userId);

        // 전시 리뷰 (새로 추가됨)
        List<ExhibitionReview> exhibitionReviews = exhibitionReviewRepository.findByUser_Id(userId);

        // 리뷰 DTO로 통합
        List<UserMeResponse.UserActivityResponse.Review> allReviews = new ArrayList<>();
        allReviews.addAll(spaceReviews.stream().map(UserMeConverter::toReview).toList());
        allReviews.addAll(exhibitionReviews.stream().map(UserMeConverter::toExhibitionReview).toList());

        // 최종 DTO 반환
        return UserMeConverter.toUserActivityResponse(exhibitionLikes, spaceLikes, allReviews);
    }

    @Override
    public UserMeResponse.UserSpacesResponse getMySpaces(Long userId) {
        List<Space> spaces = spaceRepository.findByUserId(userId);
        return UserMeConverter.toUserSpacesResponse(spaces);
    }

    @Override
    public UserMeResponse.UserExhibitionsResponse getMyExhibitions(Long userId) {
        List<Exhibition> exhibitions = exhibitionRepository.findByUserId(userId);
        return UserMeConverter.toUserExhibitionsResponse(exhibitions);
    }

    @Override
    public UserMeResponse.UserReservationsResponse getMyReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUser_Id(userId);
        return UserMeConverter.toUserReservationsResponse(reservations);
    }
}

