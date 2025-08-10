package com.project.team5backend.domain.exhibition.review.service.command;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.review.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewException;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.image.repository.ExhibitionReviewImageRepository;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionReviewCommandServiceImpl implements ExhibitionReviewCommandService {

    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ExhibitionReviewImageRepository exhibitionReviewImageRepository;
    private final ImageCommandService imageCommandService;
    private final RedisImageTracker redisImageTracker;
    @Override
    public void createExhibitionReview(
            Long exhibitionId,String email, ExhibitionReviewReqDTO.createExReviewReqDTO createExhibitionReviewReqDTO) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(()-> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        List<String> fileKeys = redisImageTracker.getOrderedFileKeysByEmail(email);

        ExhibitionReview exhibitionReview = ExhibitionReviewConverter.toEntity(createExhibitionReviewReqDTO, exhibition, user);
        exhibitionReviewRepository.save(exhibitionReview);

        for (String fileKey : fileKeys) {
            exhibitionReviewImageRepository.save(ImageConverter.toEntityExhibitionReviewImage(exhibitionReview, fileKey));
            redisImageTracker.remove("likelion@naver.com", fileKey);
        }
    }
    @Override
    public void deleteExhibitionReview(Long exhibitionReviewId) {
        ExhibitionReview exhibitionReview = exhibitionReviewRepository.findById(exhibitionReviewId)
                .orElseThrow(() -> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));

        if (exhibitionReview.isDeleted()) return;

        exhibitionReview.delete();

        // 전시이미지 소프트 삭제
        List<ExhibitionReviewImage> images = exhibitionReviewImageRepository.findByExhibitionReviewId(exhibitionReviewId);
        images.forEach(ExhibitionReviewImage::deleteImage);
        List<String> keys = images.stream().map(ExhibitionReviewImage::getFileKey).toList();

        // 리뷰가 속햇던 전시의 reviewCount--
        exhibitionReview.getExhibition().decreaseReviewCount();

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(keys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }

}
