package com.project.team5backend.domain.exhibition.review.service.command;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.review.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.exhibition.review.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewErrorCode;
import com.project.team5backend.domain.exhibition.review.exception.ExhibitionReviewException;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ExhibitionReviewImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final S3Uploader s3Uploader;
    @Override
    public void createExhibitionReview(
            Long exhibitionId,String email, ExhibitionReviewReqDTO.createExReviewReqDTO createExhibitionReviewReqDTO,
            List<MultipartFile> images) {
        LocalDate current = LocalDate.now();
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApproveAndOpening(exhibitionId, current, Status.APPROVED)
                .orElseThrow(()-> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        ExhibitionReview exhibitionReview = ExhibitionReviewConverter.toEntity(createExhibitionReviewReqDTO, exhibition, user);
        exhibitionReviewRepository.save(exhibitionReview);

        List<ExhibitionReviewImage> reviewImages = new ArrayList<>();
        for (MultipartFile file : images) {
            // fileKey: exhibitionReviews/{reviewId}/{UUID}.jpg
            String url = s3Uploader.upload(file, "exhibitionReviews");
            ExhibitionReviewImage reviewImage =
                    ImageConverter.toEntityExhibitionReviewImage(exhibitionReview, url);
            reviewImages.add(reviewImage);
        }
        exhibitionReviewImageRepository.saveAll(reviewImages);

        // 5) 리뷰 평균/카운트 갱신
        double rating = exhibitionReview.getRating();
        exhibitionRepository.applyReviewCreated(exhibitionId, rating);
    }
    @Override
    public void deleteExhibitionReview(Long exhibitionReviewId, String email) {
        ExhibitionReview exhibitionReview = exhibitionReviewRepository.findByIdAndIsDeletedFalse(exhibitionReviewId)
                .orElseThrow(() -> new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_NOT_FOUND));

        if (exhibitionReview.isDeleted()) return;

        if (!Objects.equals(exhibitionReview.getUser().getEmail(), email)) {
            throw new ExhibitionReviewException(ExhibitionReviewErrorCode.EXHIBITION_REVIEW_FORBIDDEN);
        }

        exhibitionReview.delete();

        // 전시이미지 소프트 삭제
        List<ExhibitionReviewImage> images = exhibitionReviewImageRepository.findByExhibitionReviewId(exhibitionReviewId);
        images.forEach(ExhibitionReviewImage::deleteImage);
        List<String> keys = images.stream().map(ExhibitionReviewImage::getFileKey).toList();

        Exhibition exhibition = exhibitionReview.getExhibition();
        // 리뷰 평균/카운트 갱신
        double rating = exhibitionReview.getRating();
        exhibitionRepository.applyReviewDeleted(exhibition.getId(), rating);

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(keys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }

}
