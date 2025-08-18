package com.project.team5backend.domain.exhibition.exhibition.service.admin.command;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.recommendation.repository.ExhibitionEmbeddingRepository;
import com.project.team5backend.domain.recommendation.service.EmbeddingService;
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
    private final ExhibitionEmbeddingRepository embeddingRepository;
    private final EmbeddingService embeddingService;

    @Override
    public void approveExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findPendingExhibition(exhibitionId, Status.PENDING)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.PENDING_EXHIBITION_NOT_FOUND));
        exhibition.approveStatus();

        // 이미 있으면 생략, 없으면 생성
        if (!embeddingRepository.existsById(exhibition.getId())) {
            // ★ 승인과 동시에 임베딩 생성
            embeddingService.upsertExhibitionEmbedding(exhibition);
        }
    }

    @Override
    public void rejectExhibition(Long exhibitionId) {
        LocalDate today = LocalDate.now();
        Exhibition exhibition = exhibitionRepository.findPendingExhibition(exhibitionId, Status.PENDING)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.PENDING_EXHIBITION_NOT_FOUND));
        exhibition.rejectStatus();
    }
}
