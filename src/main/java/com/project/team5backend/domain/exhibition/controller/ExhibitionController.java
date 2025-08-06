package com.project.team5backend.domain.exhibition.controller;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.service.command.ExhibitionCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "전시 API")
public class ExhibitionController {

    private final ExhibitionCommandService exhibitionCommandService;

    @PostMapping
    public CustomResponse<String> createExhibition(
            @RequestBody ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO){
        exhibitionCommandService.createExhibition(createExhibitionReqDTO);
        return CustomResponse.onSuccess("전시글 등록이 완료되었습니다. 관리자 승인 대기열에 추가합니다.");
    }

    @DeleteMapping("/{exhibitionId}")
    public CustomResponse<String> deleteExhibition(@PathVariable Long exhibitionId){
        exhibitionCommandService.deleteExhibition(exhibitionId);
        return CustomResponse.onSuccess("해당 전시가 삭제되었습니다.");
    }
}
