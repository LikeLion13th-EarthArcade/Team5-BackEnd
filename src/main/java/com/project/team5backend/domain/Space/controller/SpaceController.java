package com.project.team5backend.domain.Space.controller;

import com.project.team5backend.domain.Space.dto.request.SpaceRequest;
import com.project.team5backend.domain.Space.dto.response.SpaceResponse;
import com.project.team5backend.domain.Space.service.command.SpaceCommandService;
import com.project.team5backend.domain.Space.service.query.SpaceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SpaceResponse.CommonResponse<SpaceResponse.SpaceRegistrationResponse>> registerSpace(
            @RequestBody SpaceRequest.Create request) {
        SpaceResponse.SpaceRegistrationResponse response = spaceCommandService.registerSpace(request);
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>("200", "전시 공간 등록을 성공했습니다. 관리자 승인 대기 중입니다.", response));
    }

    @Operation(summary = "전시 공간 목록 조회")
    @GetMapping
    public ResponseEntity<SpaceResponse.CommonResponse<List<SpaceResponse.SpaceSearchResponse>>> getSpaces() {
        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.getApprovedSpaces();
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>("200", "전시 공간 목록 조회에 성공하였습니다.", spaces));
    }
    @Operation(summary = "전시 공간 검색(조건) 조회")
    @GetMapping("/search")
    public ResponseEntity<SpaceResponse.CommonResponse<List<SpaceResponse.SpaceSearchResponse>>> searchSpaces(
            @ModelAttribute SpaceRequest.Search searchRequest) {
        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.searchSpaces(searchRequest);
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>("200", "전시 공간 검색 조회에 성공하였습니다.", spaces));
    }
    @Operation(summary = "전시 공간 상세 조회")
    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceResponse.CommonResponse<SpaceResponse.SpaceDetailResponse>> getSpaceDetails(@PathVariable Long spaceId) {
        SpaceResponse.SpaceDetailResponse spaceDetail = spaceQueryService.getSpaceDetails(spaceId);
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>("200", "전시 공간 상세 조회를 성공했습니다.", spaceDetail));
    }
    @PostMapping("/{spaceId}/like")
    public ResponseEntity<SpaceResponse.CommonResponse<Map<String, Boolean>>> toggleLike(@PathVariable Long spaceId) {
        boolean liked = spaceCommandService.toggleLike(spaceId, 1L);
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>(
                "success",
                liked ? "전시 공간 좋아요 성공" : "전시 공간 좋아요를 취소했습니다.",
                Map.of("liked", liked)
        ));
    }
    @Operation(summary = "전시 공간 정보 삭제")
    @DeleteMapping("/{spaceId}")
    public ResponseEntity<SpaceResponse.CommonResponse<Void>> deleteSpace(@PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId);
        return ResponseEntity.ok(new SpaceResponse.CommonResponse<>("200", "전시 공간이 삭제되었습니다.", null));
    }
}