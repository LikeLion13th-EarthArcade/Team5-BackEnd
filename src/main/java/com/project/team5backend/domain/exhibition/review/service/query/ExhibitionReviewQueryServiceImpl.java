package com.project.team5backend.domain.exhibition.review.service.query;

import com.project.team5backend.domain.exhibition.review.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewException;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionReviewQueryServiceImpl implements ExhibitionReviewQueryService {

    private final ExhibitionReviewRepository exhibitionReviewRepository;

    @Override
    public ExhibitionReviewResDTO.exReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId) {
        ExhibitionReview exhibitionReview = exhibitionReviewRepository.findByIdAndIsDeletedFalse(exhibitionReviewId)
                .orElseThrow(()-> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));

        List<String> fileKeys = exhibitionReview.getExhibitionReviewImages().stream()
                .map(ExhibitionReviewImage::getFileKey).collect(Collectors.toList());

        return ExhibitionReviewConverter.toDetailExReviewResDTO(exhibitionReview, fileKeys);
    }

    @Override
    public Page<ExhibitionReviewResDTO.exReviewDetailResDTO> getExhibitionReviewList(
            Long exhibitionId, Pageable pageable) {
        Page<ExhibitionReview> reviewPage = exhibitionReviewRepository.findByExhibitionIdAndIsDeletedFalse(exhibitionId, pageable);


        return reviewPage.map(review -> {
            // 연관관계로 fileKeys 추출
            List<String> fileKeys = review.getExhibitionReviewImages().stream()
                    .map(ExhibitionReviewImage::getFileKey)
                    .collect(Collectors.toList());

            return ExhibitionReviewConverter.toDetailExReviewResDTO(review, fileKeys);
        });
    }
}
