package com.project.team5backend.domain.auth.controller;



import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse.CommonResponse> signUp(@RequestBody UserRequest.SignUp request) {
        authCommandService.signUp(request);
        return ResponseEntity.ok(new UserResponse.CommonResponse("success", "회원가입이 완료되었습니다.", null));
    }


    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호으로 로그인")
    @PostMapping("/login")
    public ResponseEntity<UserResponse.CommonResponse> login(@RequestBody UserRequest.Login request,
                                                             HttpServletResponse response) {
        UserResponse.LoginResult result = authCommandService.login(request, response);
        return ResponseEntity.ok(
                new UserResponse.CommonResponse("success", "로그인이 완료되었습니다.", result)
        );
    }

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 받아 소셜 로그인")
    @GetMapping("/kakao/callback")
    public ResponseEntity<UserResponse.CommonResponse> kakaoLogin(@RequestParam String code,
                                                                  HttpServletResponse response) {
        UserResponse.LoginResult result = authCommandService.kakaoLogin(code, response);
        return ResponseEntity.ok(
                new UserResponse.CommonResponse("success", "카카오 로그인이 완료되었습니다.", result)
        );
    }
    @Operation(summary = "이메일 인증코드 전송", description = "이메일로 인증코드 전송")
    @PostMapping("/send-code")
    public ResponseEntity<UserResponse.CommonResponse> sendVerificationCode(@RequestParam String email) {
        authCommandService.sendVerificationCode(email);
        return ResponseEntity.ok(
                new UserResponse.CommonResponse("success", "이메일 인증 코드가 전송되었습니다.", null)
        );
    }

    @Operation(summary = "이메일 인증코드 검증", description = "인증코드 유효성 검증")
    @PostMapping("/verify-code")
    public ResponseEntity<UserResponse.CommonResponse> verifyCode(@RequestParam String email,
                                                                  @RequestParam String code) {
        boolean result = authCommandService.verifyCode(email, code);
        return ResponseEntity.ok(
                new UserResponse.CommonResponse("success", "이메일 인증이 완료되었습니다.", result)
        );
    }
}
