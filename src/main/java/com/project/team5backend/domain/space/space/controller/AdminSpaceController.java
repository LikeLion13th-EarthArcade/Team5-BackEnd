package com.project.team5backend.domain.space.space.controller;


import com.project.team5backend.domain.space.space.dto.request.AdminSpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.space.service.AdminSpaceService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/spaces/admin")
@RequiredArgsConstructor
public class AdminSpaceController {

    private final AdminSpaceService adminSpaceService;

    @Operation(summary = "전시 공간 심사 목록 조회")
    @GetMapping("/pending")
    // @PreAuthorize("hasRole('ADMIN')") // 보안 설정: 관리자만 접근 가능
    public CustomResponse<List<AdminSpaceResponse.PendingSpaceListResponse>> getPendingSpaces() {
        List<AdminSpaceResponse.PendingSpaceListResponse> pendingSpaces = adminSpaceService.getPendingSpaces();
        return CustomResponse.onSuccess(pendingSpaces);
    }

    @Operation(summary = "전시 공간 심사 승인 or 거절")
    @PatchMapping("/{spaceId}/pending")
    // @PreAuthorize("hasRole('ADMIN')") // 보안 설정: 관리자만 접근 가능
    public CustomResponse<AdminSpaceResponse.AdminActionResponse> processSpaceApproval(
            @PathVariable Long spaceId,
            @RequestBody AdminSpaceRequest.AdminSpaceReq request) {

        AdminSpaceResponse.AdminActionResponse response = adminSpaceService.processSpaceApproval(spaceId, request);

        return CustomResponse.onSuccess(response);

    }
}