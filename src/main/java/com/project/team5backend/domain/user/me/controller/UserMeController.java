package com.project.team5backend.domain.user.me.controller;

import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.service.query.UserMeQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeQueryService userMeQueryService;

    // 인증된 사용자 ID 가져오기
    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        // auth.getPrincipal()이 CustomUserDetails 구현체라고 가정
        var userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/activity")
    @Operation(summary = "내 활동 조회", description = "내가 좋아요한 전시/공간, 내가 작성한 리뷰 조회")
    public CustomResponse<UserMeResponse.UserActivityResponse> getMyActivity() {
        Long userId = getAuthenticatedUserId();
        return CustomResponse.onSuccess(userMeQueryService.getMyActivity(userId));
    }

    @GetMapping("/spaces")
    @Operation(summary = "내가 등록한 공간 조회")
    public CustomResponse<UserMeResponse.UserSpacesResponse> getMySpaces() {
        Long userId = getAuthenticatedUserId();
        return CustomResponse.onSuccess(userMeQueryService.getMySpaces(userId));
    }

    @GetMapping("/exhibitions")
    @Operation(summary = "내가 등록한 전시 조회")
    public CustomResponse<UserMeResponse.UserExhibitionsResponse> getMyExhibitions() {
        Long userId = getAuthenticatedUserId();
        return CustomResponse.onSuccess(userMeQueryService.getMyExhibitions(userId));
    }

    @GetMapping("/reservations")
    @Operation(summary = "내가 예약한 공간 조회")
    public CustomResponse<UserMeResponse.UserReservationsResponse> getMyReservations() {
        Long userId = getAuthenticatedUserId();
        return CustomResponse.onSuccess(userMeQueryService.getMyReservations(userId));
    }
}
