package com.project.team5backend.domain.exhibition.exhibition.service.command;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;

public interface ExhibitionCommandService {

    void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO);

    void deleteExhibition(Long id);
}
