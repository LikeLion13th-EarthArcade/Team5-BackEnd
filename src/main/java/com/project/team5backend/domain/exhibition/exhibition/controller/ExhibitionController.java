package com.project.team5backend.domain.exhibition.exhibition.controller;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.service.command.ExhibitionCommandService;
import com.project.team5backend.domain.exhibition.exhibition.service.query.ExhibitionQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return CustomResponse.onSuccess(exhibitionCommandService.previewExhibition(createExhibitionReqDTO));
    }
    @PostMapping("/{exhibitionId}/like")
    @Operation(summary = "전시 좋아요", description = "좋아요 없으면 등록, 있으면 취소")
    public CustomResponse<ExhibitionResDTO.LikeExhibitionResDTO> likeExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionCommandService.likeExhibition(exhibitionId));
    }

    @GetMapping("/{exhibitionId}")
    @Operation(summary = "전시 상세 보기", description = "전시 상세 보기 api")
    public CustomResponse<ExhibitionResDTO.DetailExhibitionResDTO> detailExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionQueryService.detailExhibition(exhibitionId));
    }

    @DeleteMapping("/{exhibitionId}")
    @Operation(summary = "전시 삭제", description = "전시가 삭제된 전시로 변경하는 api")
    public CustomResponse<String> deleteExhibition(@PathVariable Long exhibitionId){
        exhibitionCommandService.deleteExhibition(exhibitionId);
        return CustomResponse.onSuccess("해당 전시가 삭제되었습니다.");
    }
}
