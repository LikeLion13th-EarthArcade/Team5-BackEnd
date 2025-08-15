package com.project.team5backend.domain.user.controller;


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

    @Operation(summary = "회원정보 수정", description = "회원 이름 수정")
    @PatchMapping("/name")
    public CustomResponse<String> updateName(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String name) {
        Long userId = userDetails.getUserId();
        commandService.updateName(userId, name);
        return CustomResponse.onSuccess("회원 이름 수정 완료");
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/password")
    public CustomResponse<String> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestParam String currentPassword, @RequestParam String newPassword) {
        Long userId = userDetails.getUserId();
        commandService.changePassword(userId, currentPassword, newPassword);
        return CustomResponse.onSuccess("비밀번호 변경 완료");
    }

    @Operation(summary = "회원 탈퇴", description = "계정 탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<CustomResponse<String>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  HttpServletResponse response) {
        Long userId = userDetails.getUserId();
        commandService.deleteUser(userId); // 사용자 삭제
        // JSESSIONID는 Spring Security가 자동으로 만료시키므로 별도의 쿠키 삭제 로직이 필요하지 않움

        CustomResponse<String> customResponse = CustomResponse.onSuccess("회원 탈퇴 완료");
        return ResponseEntity.ok(customResponse);
    }

}
