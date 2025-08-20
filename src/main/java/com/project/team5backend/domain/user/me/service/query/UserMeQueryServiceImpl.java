package com.project.team5backend.domain.user.me.service.query;


import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.space.reservation.entity.Reservation;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.me.converter.UserMeConverter;
import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.entity.Activity;
import com.project.team5backend.domain.user.me.repository.ActivityRepository;
import com.project.team5backend.domain.space.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMeQueryServiceImpl implements UserMeQueryService {

    private final ActivityRepository activityRepository;
    private final SpaceRepository spaceRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public UserMeResponse.UserActivityResponse getMyActivity(Long userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return UserMeConverter.toUserActivityResponse(activities);
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
        List<Reservation> reservations = reservationRepository.findBySpaceUser_Id(userId);
        return UserMeConverter.toUserReservationsResponse(reservations);
    }
}
