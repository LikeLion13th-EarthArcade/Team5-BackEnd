package com.project.team5backend.domain.space.controller;


import com.project.team5backend.domain.space.dto.request.AdminSpaceRequest;
import com.project.team5backend.domain.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.service.AdminSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AdminSpaceResponse.CommonResponse<List<AdminSpaceResponse.PendingSpaceListResponse>>> getPendingSpaces() {
        List<AdminSpaceResponse.PendingSpaceListResponse> pendingSpaces = adminSpaceService.getPendingSpaces();
        return ResponseEntity.ok(new AdminSpaceResponse.CommonResponse<>("200", "공간 심사 목록 조회 성공", pendingSpaces));
    }

    @Operation(summary = "전시 공간 심사 승인 or 거절")
    @PatchMapping("/{spaceId}/pending")
    // @PreAuthorize("hasRole('ADMIN')") // 보안 설정: 관리자만 접근 가능
    public ResponseEntity<AdminSpaceResponse.CommonResponse<AdminSpaceResponse.AdminActionResponse>> processSpaceApproval(
            @PathVariable Long spaceId,
            @RequestBody AdminSpaceRequest request) {

        AdminSpaceResponse.AdminActionResponse response = adminSpaceService.processSpaceApproval(spaceId, request);

        String message = request.isApprove() ? "공간이 승인되었습니다." : "공간이 거절되었습니다.";
        return ResponseEntity.ok(new AdminSpaceResponse.CommonResponse<>("200", message, response));
    }
}