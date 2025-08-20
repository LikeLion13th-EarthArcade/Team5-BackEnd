package com.project.team5backend.domain.user.me.service.query;


import com.project.team5backend.domain.user.user.dto.response.UserResponse;

public interface UserMeQueryService {
    UserResponse getUserMypage(Long userId);
}

