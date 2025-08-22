package com.project.team5backend.domain.user.user.service.command;

public interface UserCommandService {
    void updateName(Long userId, String newName);
    void changePassword(Long userId, String currentPassword, String newPassword);
    void deleteUser(Long userId);
}