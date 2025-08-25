package com.project.team5backend.domain.exhibition.exhibition.controller.admin;

import com.project.team5backend.domain.exhibition.exhibition.dto.admin.response.ExhibitionAdminResDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.service.admin.command.ExhibitionAdminCommandService;
import com.project.team5backend.domain.exhibition.exhibition.service.admin.query.ExhibitionAdminQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/exhibitions")
@RequiredArgsConstructor
@Tag(name = "Admin 전시 API")
public class ExhibitionAdminController {

    private final ExhibitionAdminQueryService exhibitionAdminQueryService;
    private final ExhibitionAdminCommandService exhibitionAdminCommandService;

    @Operation(summary = "전시 승인 (admin)", description = "status가 pending인 전시 approve로 변경")
    @PostMapping("/{exhibitionId}/approve")
    public CustomResponse<String> approveExhibition(@PathVariable Long exhibitionId) {
        exhibitionAdminCommandService.approveExhibition(exhibitionId);
        return CustomResponse.onSuccess("전시가 승인되었습니다.");
    }

    @Operation(summary = "전시 거절 (admin)", description = "status가 pending인 전시 reject로 변경")
    @PostMapping("/{exhibitionId}/reject")
    public CustomResponse<String> rejectExhibition(@PathVariable Long exhibitionId) {
        exhibitionAdminCommandService.rejectExhibition(exhibitionId);
        return CustomResponse.onSuccess("전시가 거부되었습니다.");
    }

    @Operation(summary = "전시 심사 목록 조회 (admin)", description = "status가 pending인 전시 목록 조회")
    @GetMapping()
    public CustomResponse<Page<ExhibitionAdminResDTO.PendingExhibitionResDTO>> getAllPendingExhibitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(exhibitionAdminQueryService.getPendingExhibitions(pageable));
    }

    @Operation(summary = "전시 상세 보기 (admin)", description = "pending인 전시 상세 보기 ")
    @GetMapping("/{exhibitionId}")
    public CustomResponse<ExhibitionResDTO.DetailPendingExhibitionResDTO> detailPendingExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionAdminQueryService.getDetailPendingExhibition(exhibitionId));
    }
}
