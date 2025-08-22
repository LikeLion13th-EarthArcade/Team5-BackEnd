package com.project.team5backend.domain.exhibition.exhibition.service.admin.query;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.converter.admin.ExhibitionAdminConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.admin.response.ExhibitionAdminResDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionAdminQueryServiceImpl implements ExhibitionAdminQueryService{

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;

    @Override
    public Page<ExhibitionAdminResDTO.PendingExhibitionResDTO> getPendingExhibitions(Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<Exhibition> page = exhibitionRepository.findPendingExhibitions(today, pageable, Status.PENDING);
        return page.map(ExhibitionAdminConverter::toPendingExhibitionResDTO);
    }

    @Override
    public ExhibitionResDTO.DetailExhibitionResDTO getDetailPendingExhibition(Long exhibitionId){
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(()-> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
        List<String> imageFileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(exhibitionId);
        return ExhibitionConverter.toDetailExhibitionResDTO(exhibition, imageFileKeys);
    }
}
