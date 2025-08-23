package com.project.team5backend.domain.user.user.service.command;

import com.project.team5backend.domain.user.user.dto.request.UserRequest;

public interface UserCommandService {
    void updateUserInfo(Long userId, UserRequest.UpdateUserInfo request);
    void deleteUser(Long userId);
}