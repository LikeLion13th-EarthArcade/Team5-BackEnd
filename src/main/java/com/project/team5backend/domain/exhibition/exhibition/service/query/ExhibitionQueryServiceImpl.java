package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    @Override
    public ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long id) {
        Exhibition exhibition = exhibitionRepository.findById(id)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // 전시 이미지들의 fileKey만 조회
        List<String> imageFileKeys = exhibitionImageRepository.findFileKeysByExhibitionId(2L);

        return ExhibitionConverter.toDetailExhibitionResDTO(exhibition, imageFileKeys);
    }

}
