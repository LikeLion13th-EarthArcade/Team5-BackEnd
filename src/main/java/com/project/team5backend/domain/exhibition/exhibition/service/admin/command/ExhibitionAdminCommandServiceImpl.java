package com.project.team5backend.domain.exhibition.exhibition.service.admin.command;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionAdminCommandServiceImpl implements ExhibitionAdminCommandService {

    private final ExhibitionRepository exhibitionRepository;

    @Override
    public void approveExhibition(Long exhibitionId) {
        LocalDate today = LocalDate.now();
        Exhibition exhibition = exhibitionRepository.findPendingExhibition(exhibitionId, today, Status.PENDING)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.PENDING_EXHIBITION_NOT_FOUND));
        exhibition.approveStatus();
    }

    @Override
    public void rejectExhibition(Long exhibitionId) {
        LocalDate today = LocalDate.now();
        Exhibition exhibition = exhibitionRepository.findPendingExhibition(exhibitionId, today, Status.PENDING)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.PENDING_EXHIBITION_NOT_FOUND));
        exhibition.rejectStatus();
    }
}
