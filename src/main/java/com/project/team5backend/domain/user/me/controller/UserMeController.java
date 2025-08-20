package com.project.team5backend.domain.user.me.controller;


import com.project.team5backend.domain.user.me.dto.response.UserMeResponse;
import com.project.team5backend.domain.user.me.service.query.UserMeQueryService;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeQueryService userMeQueryService;
    private final UserRepository userRepository;

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        // principal을 String으로 받음 (JWT 또는 커스텀 세션일 경우)
        String email;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            email = ((CustomUserDetails) auth.getPrincipal()).getEmail();
        } else if (auth.getPrincipal() instanceof String) {
            email = (String) auth.getPrincipal();
        } else {
            throw new IllegalStateException("알 수 없는 인증 principal 타입: " + auth.getPrincipal().getClass());
        }

        // DB에서 사용자 조회 후 ID 반환
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."))
                .getId();
    }

    @GetMapping("/activity")
    @Operation(summary = "내 활동 조회")
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

