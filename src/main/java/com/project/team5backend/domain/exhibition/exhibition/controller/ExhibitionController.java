package com.project.team5backend.domain.exhibition.exhibition.controller;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionSort;
import com.project.team5backend.domain.exhibition.exhibition.service.command.ExhibitionCommandService;
import com.project.team5backend.domain.exhibition.exhibition.service.query.ExhibitionQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "전시 API")
public class ExhibitionController {

    private final ExhibitionCommandService exhibitionCommandService;
    private final ExhibitionQueryService exhibitionQueryService;

    @PostMapping
    @Operation(summary = "전시 생성", description = "전시 생성하면 전시 객체가 심사 대상에 포함됩니다.")
    public CustomResponse<String> createExhibition(
            @RequestBody ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO){
        exhibitionCommandService.createExhibition(createExhibitionReqDTO);
        return CustomResponse.onSuccess("전시글 등록이 완료되었습니다. 관리자 승인 대기열에 추가합니다.");
    }

    @PostMapping("/preview")
    @Operation(summary = "전시 생성 중 미리보기", description = "작성중 미리보기 api")
    public CustomResponse<ExhibitionResDTO.PreviewExhibitionResDTO> previewExhibition(
            @RequestBody ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO) {
        return CustomResponse.onSuccess(exhibitionCommandService.previewExhibition("likelion@naver.com",createExhibitionReqDTO));
    }
    @PostMapping("/{exhibitionId}/like")
    @Operation(summary = "전시 좋아요", description = "좋아요 없으면 등록, 있으면 취소")
    public CustomResponse<ExhibitionResDTO.LikeExhibitionResDTO> likeExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionCommandService.likeExhibition(exhibitionId));
    }

    @GetMapping("/{exhibitionId}")
    @Operation(summary = "전시 상세 보기", description = "전시 상세 보기 api")
    public CustomResponse<ExhibitionResDTO.DetailExhibitionResDTO> detailExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionQueryService.getDetailExhibition(exhibitionId));
    }

    @Operation(summary = "전시 검색", description = "전시 검색하면 한페이지에 4개의 전시와 서울 시청 중심의 위도 경도 반환")
    @GetMapping("/search")
    public CustomResponse<ExhibitionResDTO.SearchExhibitionPageResDTO> searchExhibitions(
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "distinct", required = false) String district,
            @RequestParam(name = "mood", required = false) Mood mood,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate,
            @RequestParam(defaultValue = "new") ExhibitionSort sort,   // new | old | popular
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(exhibitionQueryService.searchExhibition(category, district, mood, localDate, sort, page));
    }

    @Operation(summary = "지금 뜨는 전시회", description = "현재 진행중인 전시중에서 reviewCount가 가장 높은 전시 반환")
    @GetMapping("/hot-now")
    public CustomResponse<ExhibitionResDTO.HotNowExhibitionResDTO> hotNowExhibition() {
        return CustomResponse.onSuccess(exhibitionQueryService.getHotNowExhibition());
    }

    @Operation(summary = "지금 뜨는, 다가오는 전시회", description = "아직 시작되지 않은 전시중에서 likeCount가 가장 높은 전시 반환")
    @GetMapping("/upcoming-popularity")
    public CustomResponse<ExhibitionResDTO.UpcomingPopularityExhibitionResDTO> upcomingPopularityExhibition() {
        return CustomResponse.onSuccess(exhibitionQueryService.getUpcomingPopularityExhibition());
    }

    @DeleteMapping("/{exhibitionId}")
    @Operation(summary = "전시 삭제", description = "전시가 삭제된 전시로 변경하는 api")
    public CustomResponse<String> deleteExhibition(@PathVariable Long exhibitionId){
        //유저 검증 로직 필요
        exhibitionCommandService.deleteExhibition(4L);
        return CustomResponse.onSuccess("해당 전시가 삭제되었습니다.");
    }
}
