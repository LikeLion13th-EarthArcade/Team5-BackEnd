package com.project.team5backend.domain.auth.controller;


import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입")
    @PostMapping("/signup")
    public CustomResponse<String> signUp(@RequestBody UserRequest.SignUp request) {
        authCommandService.signUp(request);
        return CustomResponse.onSuccess("회원가입을 성공했습니다.");
    }

    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호으로 로그인")
    @PostMapping("/login")
    public CustomResponse<UserResponse.LoginResult> login(@RequestBody UserRequest.Login request,
                                                             HttpServletResponse response) {
        UserResponse.LoginResult result = authCommandService.login(request, response);
        return CustomResponse.onSuccess(result);
    }

    @Operation(summary = "이메일 인증코드 전송", description = "이메일로 인증코드 전송")
    @PostMapping("/send-code")
    public CustomResponse<String> sendVerificationCode(@RequestParam String email) {
        authCommandService.sendVerificationCode(email);
        return CustomResponse.onSuccess("이메일 인증 코드를 전송했습니다.");
    }

    @Operation(summary = "이메일 인증코드 검증", description = "인증코드 유효성 검증")
    @PostMapping("/verify-code")
    public CustomResponse<Boolean> verifyCode(@RequestParam String email,
                                                                  @RequestParam String code) {
        boolean result = authCommandService.verifyCode(email, code);
        return CustomResponse.onSuccess(result);
    }
}
