package com.project.team5backend.domain.exhibition.exhibition.service.admin.command;

public interface ExhibitionAdminCommandService {
    void approveExhibition(Long exhibitionId);

    void rejectExhibition(Long exhibitionId);
}
