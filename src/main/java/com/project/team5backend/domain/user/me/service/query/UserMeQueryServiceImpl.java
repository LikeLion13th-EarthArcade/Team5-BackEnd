package com.project.team5backend.domain.user.me.service.query;


import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.me.converter.UserConverter;
import com.project.team5backend.domain.user.user.dto.response.UserResponse;
import com.project.team5backend.domain.space.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMeQueryServiceImpl implements UserMeQueryService {

    private final ActivityRepository activityRepository;
    private final SpaceRepository spaceRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public UserResponse getUserMypage(Long userId) {
        var activities = activityRepository.findByUserId(userId);
        var spaces = spaceRepository.findByUserId(userId);
        var exhibitions = exhibitionRepository.findByUserId(userId);
        var reservations = reservationRepository.findByUserId(userId);

        return UserConverter.toUserResponse(activities, spaces, exhibitions, reservations);
    }
}
