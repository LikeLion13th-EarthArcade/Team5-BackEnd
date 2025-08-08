package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.domain.user.service.query.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService queryService;
    private final UserCommandService commandService;
    @Autowired
    private Map<String, User> sessionStore;

    private Long getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        //쿠키 배열이 null인지 확인
        if (cookies == null) {
            System.out.println("[쿠키 로그] 요청에 쿠키가 없습니다.");
            throw new IllegalArgumentException("인증 쿠키가 없습니다.");
        }

        //쿠키를 순회하며 "SESSION" 쿠키를 찾는 로그
        for (Cookie cookie : cookies) {
            System.out.println("[쿠키 로그] 찾은 쿠키: " + cookie.getName() + " = " + cookie.getValue());
            if ("SESSION".equals(cookie.getName())) {
                String sessionId = cookie.getValue();
                User user = sessionStore.get(sessionId);

                // 세션 저장소에서 사용자를 찾았는지 확인하는 로그
                if (user != null) {
                    System.out.println("[세션 로그] SESSION ID로 사용자 찾기 성공! USER ID: " + user.getId());
                    return user.getId();
                } else {
                    System.out.println("[세션 로그] SESSION ID(" + sessionId + ")에 해당하는 사용자가 세션 저장소에 없습니다.");
                    throw new IllegalArgumentException("인증 쿠키가 유효하지 않습니다.");
                }
            }
        }

        // "SESSION" 쿠키를 찾지 못한 경우의 로그
        System.out.println("[쿠키 로그] 'SESSION' 이름의 쿠키를 찾지 못했습니다.");
        throw new IllegalArgumentException("인증 쿠키가 없습니다.");
    }

    @Operation(summary = "회원정보 조회", description = "마이페이지에서 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserResponse.CommonResponse> getMyInfo(HttpServletRequest request) {
        Long userId = getUserIdFromCookie(request);
        UserResponse.MyInfo result = queryService.getMyInfo(userId);
        return ResponseEntity.ok(new UserResponse.CommonResponse("success", "회원 정보 조회 성공", result));
    }

    @Operation(summary = "회원정보 수정", description = "회원 이름 수정")
    @PatchMapping("/name")
    public ResponseEntity<UserResponse.CommonResponse> updateName(HttpServletRequest request, @RequestParam String name) {
        Long userId = getUserIdFromCookie(request);
        commandService.updateName(userId, name);
        return ResponseEntity.ok(new UserResponse.CommonResponse("success", "회원 이름 수정 완료", null));
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/password")
    public ResponseEntity<UserResponse.CommonResponse> updatePassword(HttpServletRequest request,
                                                                      @RequestParam String currentPassword,
                                                                      @RequestParam String newPassword) {
        Long userId = getUserIdFromCookie(request);
        commandService.changePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok(new UserResponse.CommonResponse("success", "비밀번호 변경 완료", null));
    }

    @Operation(summary = "회원 탈퇴", description = "계정 탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<UserResponse.CommonResponse> deleteUser(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        Long userId = getUserIdFromCookie(request);
        commandService.deleteUser(userId); // 실제 사용자 삭제

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("USER_ID", null)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(new UserResponse.CommonResponse("success", "회원 탈퇴 완료", null));
    }

}
