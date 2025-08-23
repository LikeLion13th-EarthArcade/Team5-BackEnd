package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.dto.response.UserResponse.MyInfo;

import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.domain.user.service.query.UserQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService queryService;
    private final UserCommandService commandService;

    @Operation(summary = "회원정보 조회", description = "마이페이지에서 정보 조회")
    @GetMapping("/me")
    public CustomResponse<UserResponse.MyInfo> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails ){
        Long userId = userDetails.getUserId();
        UserResponse.MyInfo result = queryService.getMyInfo(userId);
        return CustomResponse.onSuccess(result);
    }

    @Operation(summary = "회원정보 수정", description = "회원 이름 및 비밀번호 변경")
    @PatchMapping
    public CustomResponse<String> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserRequest.UpdateUserInfo request) {
        Long userId = userDetails.getUserId();
        commandService.updateUserInfo(userId, request);
        return CustomResponse.onSuccess("회원정보 수정 완료");
    }

    @Operation(summary = "회원 탈퇴", description = "계정 탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        Long userId = userDetails.getUserId();
        commandService.deleteUser(userId); // 사용자 삭제
        // ✅ 세션 무효화 + 쿠키 삭제
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        CustomResponse<String> customResponse = CustomResponse.onSuccess("회원 탈퇴 완료");
        return ResponseEntity.ok(customResponse);
    }

}
