package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.space.service.query.SpaceQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
        return CustomResponse.onSuccess(null);    }
}