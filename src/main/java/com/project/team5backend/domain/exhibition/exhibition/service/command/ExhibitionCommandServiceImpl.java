package com.project.team5backend.domain.exhibition.exhibition.service.command;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionLikeConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.entity.embedded.Address;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionCommandServiceImpl implements ExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final RedisImageTracker redisImageTracker;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ImageCommandService imageCommandService;
    private final AddressService addressService;

    @Override
    public void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        if (redisImageTracker.getImageCountByEmail("likelion@naver.com") == 0) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        //이미지 가져오기
        List<String> fileKeys = redisImageTracker.getOrderedFileKeysByEmail("likelion@naver.com");
        //주소 가져오기
        AddressResDTO.AddressCreateResDTO addressResDTO = addressService.resolve(createExhibitionReqDTO.address());
        Address address = AddressConverter.toAddress(addressResDTO);

        Exhibition ex = ExhibitionConverter.toEntity(user, createExhibitionReqDTO, fileKeys.get(0), address);
        exhibitionRepository.save(ex); //전시 등록

        for (String fileKey : fileKeys) {
            exhibitionImageRepository.save(ImageConverter.toEntityExhibitionImage(ex, fileKey));
            redisImageTracker.remove("likelion@naver.com", fileKey);
        }
    }

    @Override
    public ExhibitionResDTO.LikeExhibitionResDTO likeExhibition(Long exhibitionId) {
        User user = userRepository.findById(1L)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        if (exhibitionLikeRepository.existsByUserIdAndExhibitionId(user.getId(), exhibition.getId())) {
            //좋아요 취소
            exhibitionLikeRepository.deleteByUserIdAndExhibitionId(user.getId(), exhibitionId);
            exhibition.decreaseLikeCount();
            return ExhibitionLikeConverter.toLikeExhibitionResDTO(exhibitionId, "관심목록에서 삭제되었습니다.");
        }else {
            //좋아요 등록
            exhibitionLikeRepository.save(ExhibitionLikeConverter.toEntity(user, exhibition));
            exhibition.increaseLikeCount();
            return ExhibitionLikeConverter.toLikeExhibitionResDTO(exhibitionId, "관심목록에 추가되었습니다.");
        }
    }


    @Override
    public ExhibitionResDTO.PreviewExhibitionResDTO previewExhibition(String email, ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO){
        List<String> images = redisImageTracker.getOrderedFileKeysByEmail(email);
        return ExhibitionConverter.toPreviewExhibitionResDTO(createExhibitionReqDTO, images);
    }

    @Override
    public void deleteExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprove(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        if (exhibition.isDeleted()) return;

        exhibition.delete();

        // 전시이미지 소프트 삭제
        List<ExhibitionImage> images = exhibitionImageRepository.findByExhibitionId(exhibitionId);
        images.forEach(ExhibitionImage::deleteImage);
        List<String> keys = images.stream().map(ExhibitionImage::getFileKey).toList();
        // 3) 좋아요 하드 삭제 (벌크)
        exhibitionLikeRepository.deleteByExhibitionId(exhibitionId);

        // 4) 리뷰 소프트 삭제 (벌크)
        exhibitionReviewRepository.softDeleteByExhibitionId(exhibitionId);

        // 집계 초기화
        exhibition.resetCount();

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(keys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
}
