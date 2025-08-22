package com.project.team5backend.domain.user.me.service.query;


import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
public interface UserMeQueryService {
    UserMeResponse.UserActivityResponse getMyActivity(Long userId);

    UserMeResponse.UserSpacesResponse getMySpaces(Long userId);

    UserMeResponse.UserExhibitionsResponse getMyExhibitions(Long userId);

    UserMeResponse.UserReservationsResponse getMyReservations(Long userId);
}

