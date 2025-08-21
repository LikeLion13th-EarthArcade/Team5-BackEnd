package com.project.team5backend.domain.recommendation.controller;

import com.project.team5backend.domain.recommendation.dto.response.RecommendResDTO;
import com.project.team5backend.domain.recommendation.service.RecommendationService;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations/personalized")
@Tag(name = "ai 취향기반분석 API")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository; // 프로젝트에 맞게

    @Operation(summary = "ai 취향 기반 분석", description = "홈페이지에 띄우는 취향 기반 분석 api")
    @GetMapping("/summary")
    public CustomResponse<RecommendResDTO.PersonalizedSummaryResDTO> summary(
            //인증애너테이션 사용 예정
    ) {
        //Long userId = resolveUserId(user);
        Long userId = 1L;
        return CustomResponse.onSuccess(recommendationService.summary(userId));
    }
    @Operation(summary = "ai 취향 기반 분석 자세히보기", description = "취향 기반 분석을 자세히 보기 했을 때 4개의 결과 반환")
    @GetMapping("/detail")
    public CustomResponse<RecommendResDTO.PersonalizedDetailResDTO> detail(
            //인증애너테이션 사용 예정
    ) {
        //Long userId = resolveUserId(user);
        Long userId = 1L;
        return CustomResponse.onSuccess(recommendationService.detail(userId));
    }

//    private Long resolveUserId(CustomUserDetails user){
//        if (user == null) return null;
//        if (user.getId() != null) return user.getId();
//        if (user.getEmail() != null)
//            return userRepository.findIdByEmail(user.getEmail()).orElse(null);
//        return null;
//    }
}