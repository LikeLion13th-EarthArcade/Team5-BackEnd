package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthCommandService {
    void signUp(UserRequest.SignUp request);
    UserResponse.LoginResult login(UserRequest.Login request, HttpServletRequest httpRequest, HttpServletResponse response);

    // 이메일 인증
    void sendVerificationCode(String email);
    boolean verifyCode(String email, String code);

    // 로그아웃 메서드
    void logout(HttpServletRequest httpRequest);

}
