package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.space.service.query.SpaceQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;

    @Operation(summary = "전시 공간 등록")
    @PostMapping
    public CustomResponse<SpaceResponse.SpaceRegistrationResponse> registerSpace(
            @RequestBody SpaceRequest.Create request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("로그인 필요");
        }
        SpaceResponse.SpaceRegistrationResponse response = spaceCommandService.registerSpace(request);
        return CustomResponse.onSuccess(response);
    }

    @Operation(summary = "전시 공간 목록 조회")
    @GetMapping
    public CustomResponse<List<SpaceResponse.SpaceSearchResponse>> getSpaces() {
        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.getApprovedSpaces();
        return CustomResponse.onSuccess(spaces);
    }
    @Operation(summary = "전시 공간 검색(조건) 조회")
    @GetMapping("/search")
    public CustomResponse<List<SpaceResponse.SpaceSearchResponse>> searchSpaces(
            @ModelAttribute SpaceRequest.Search searchRequest) {
        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.searchSpaces(searchRequest);
        return CustomResponse.onSuccess(spaces);
    }
    @Operation(summary = "전시 공간 상세 조회")
    @GetMapping("/{spaceId}")
    public CustomResponse<SpaceResponse.SpaceDetailResponse> getSpaceDetails(@PathVariable Long spaceId) {
        SpaceResponse.SpaceDetailResponse spaceDetail = spaceQueryService.getSpaceDetails(spaceId);
        return CustomResponse.onSuccess(spaceDetail);
    }
    @Operation(summary = "전시 공간 좋아요 / 좋아요 취소")
    @PostMapping("/{spaceId}/like")
    public CustomResponse<Map<String, Boolean>> toggleLike(@PathVariable Long spaceId) {
        boolean liked = spaceCommandService.toggleLike(spaceId, 1L);
        return CustomResponse.onSuccess(Map.of("liked", liked));
    }
    @Operation(summary = "전시 공간 정보 삭제")
    @DeleteMapping("/{spaceId}")
    public CustomResponse<Void> deleteSpace(@PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId);
        return CustomResponse.onSuccess(null);
    }
    // 공간 정보 수정 API
    @PatchMapping("/{spaceId}")
    @Operation(summary = "공간 정보 수정", description = "등록한 공간의 정보 수정")
    public CustomResponse<Void> updateSpace(
            @PathVariable Long spaceId,
            @RequestBody SpaceRequest.UpdateSpace requestDto
    ) {
        spaceCommandService.updateSpace(spaceId, requestDto);
        return CustomResponse.onSuccess(null);
    }
}