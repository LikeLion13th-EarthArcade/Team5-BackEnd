package com.project.team5backend.domain.user.user.service.query;


import com.project.team5backend.domain.user.user.dto.response.UserResponse;

public interface UserQueryService {
    // 회원 정보 수정(이름)
    UserResponse.MyInfo getMyInfo(Long userId);
}

