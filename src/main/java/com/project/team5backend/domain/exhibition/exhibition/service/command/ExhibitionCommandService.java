package com.project.team5backend.domain.exhibition.exhibition.service.command;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;

public interface ExhibitionCommandService {

    void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO, String email);

    ExhibitionResDTO.LikeExhibitionResDTO likeExhibition(Long exhibitionId, String email);

    ExhibitionResDTO.PreviewExhibitionResDTO previewExhibition(String email, ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO);

    void deleteExhibition(Long id, String email);
}
