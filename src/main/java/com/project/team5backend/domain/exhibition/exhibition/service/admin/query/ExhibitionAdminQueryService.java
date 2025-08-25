package com.project.team5backend.domain.exhibition.exhibition.service.admin.query;

import com.project.team5backend.domain.exhibition.exhibition.dto.admin.response.ExhibitionAdminResDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionAdminQueryService {
    Page<ExhibitionAdminResDTO.PendingExhibitionResDTO> getPendingExhibitions(Pageable pageable);

    ExhibitionResDTO.DetailPendingExhibitionResDTO getDetailPendingExhibition(Long exhibitionId);
}
