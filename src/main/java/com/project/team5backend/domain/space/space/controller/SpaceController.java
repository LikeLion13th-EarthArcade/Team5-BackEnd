package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.space.service.query.SpaceQueryService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;
    private final UserRepository userRepository;

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        String email;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            email = ((CustomUserDetails) auth.getPrincipal()).getEmail();
        } else if (auth.getPrincipal() instanceof String) {
            email = (String) auth.getPrincipal();
        } else {
            throw new IllegalStateException("알 수 없는 인증 principal 타입: " + auth.getPrincipal().getClass());
        }

        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."))
                .getId();
    }

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
        Long userId = getAuthenticatedUserId(); // 이 부분을 수정합니다.
        boolean liked = spaceCommandService.toggleLike(spaceId, userId);
        return CustomResponse.onSuccess(Map.of("liked", liked));
    }
    @Operation(summary = "전시 공간 정보 삭제")
    @DeleteMapping("/{spaceId}")
    public CustomResponse<Void> deleteSpace(@PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId);
        return CustomResponse.onSuccess(null);    }
}