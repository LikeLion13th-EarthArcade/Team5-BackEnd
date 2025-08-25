package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionSort;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpaceSize;
import com.project.team5backend.domain.space.space.entity.SpaceType;
import com.project.team5backend.domain.space.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.space.service.query.SpaceQueryService;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;
    private final UserRepository userRepository;

    @SwaggerBody(content = @Content(
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
            }
    ))
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "전시 공간 등록", description = "로그인한 사용자가 전시 공간을 등록합니다.")
    public CustomResponse<SpaceResponse.SpaceRegistrationResponse> registerSpace(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") @Valid SpaceRequest.Create request,
            @RequestPart("images") List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty()) throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        if (images.size() > 5) throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);

        SpaceResponse.SpaceRegistrationResponse response = spaceCommandService.registerSpace(request, userDetails.getEmail(), images);
        return CustomResponse.onSuccess(response);
    }

    @Operation(summary = "전시 공간 목록 조회")
    @GetMapping
    public CustomResponse<List<SpaceResponse.SpaceSearchResponse>> getSpaces() {
        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.getApprovedSpaces();
        return CustomResponse.onSuccess(spaces);
    }

    @Operation(summary = "전시 공간 검색")
    @GetMapping("/search")
    public CustomResponse<SpaceResponse.SpaceSearchPageResponse> searchSpaces(
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "district", required = false) String district,
            @RequestParam(name = "size", required = false) SpaceSize size,
            @RequestParam(name = "type", required = false) SpaceType type,
            @RequestParam(name = "mood", required = false) SpaceMood mood,
            @RequestParam(name = "page", defaultValue = "0") int page
            ) {
        return CustomResponse.onSuccess(
                spaceQueryService.searchSpaces(startDate, endDate, district, size, type, mood, page)
        );
    }
    @Operation(summary = "전시 공간 상세 조회")
    @GetMapping("/{spaceId}")
    public CustomResponse<SpaceResponse.SpaceDetailResponse> getSpaceDetails(@PathVariable Long spaceId) {
        SpaceResponse.SpaceDetailResponse spaceDetail = spaceQueryService.getSpaceDetails(spaceId);
        return CustomResponse.onSuccess(spaceDetail);
    }
    @Operation(summary = "전시 공간 좋아요 / 좋아요 취소")
    @PostMapping("/{spaceId}/like")
    public CustomResponse<Map<String, Boolean>> toggleLike(@PathVariable Long spaceId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        boolean liked = spaceCommandService.toggleLike(spaceId, userId);
        return CustomResponse.onSuccess(Map.of("liked", liked));
    }
    @Operation(summary = "전시 공간 정보 삭제")
    @DeleteMapping("/{spaceId}")
    public CustomResponse<Void> deleteSpace(@PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId);
        return CustomResponse.onSuccess(null);    }
}