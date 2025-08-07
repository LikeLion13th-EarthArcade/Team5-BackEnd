package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.domain.user.service.query.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService queryService;
    private final UserCommandService commandService;

    private Long getUserIdFromCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("USER_ID")) {
                return Long.parseLong(cookie.getValue());
            }
        }
        throw new IllegalArgumentException("인증 쿠키가 없습니다.");
    }

    @Operation(summary = "회원정보 조회", description = "마이페이지에서 정보 조회")
    @GetMapping("/me")
    public UserResponse.MyInfo getMyInfo(HttpServletRequest request) {
        Long userId = getUserIdFromCookie(request);
        return queryService.getMyInfo(userId);
    }

    @Operation(summary = "회원정보 수정", description = "회원 이름 수정")
    @PatchMapping("/name")
    public void updateName(HttpServletRequest request, @RequestParam String name) {
        Long userId = getUserIdFromCookie(request);
        commandService.updateName(userId, name);
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/password")
    public void updatePassword(HttpServletRequest request,
                               @RequestParam String currentPassword,
                               @RequestParam String newPassword) {
        Long userId = getUserIdFromCookie(request);
        commandService.changePassword(userId, currentPassword, newPassword);
    }

    @Operation(summary = "회원 탈퇴", description = "계정 탈퇴")
    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("SESSION", null)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok("로그아웃 완료");
    }

}
