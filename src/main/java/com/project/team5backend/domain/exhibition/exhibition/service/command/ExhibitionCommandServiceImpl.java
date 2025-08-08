package com.project.team5backend.domain.exhibition.exhibition.service.command;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionLikeConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionCommandServiceImpl implements ExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;

    @Override
    public void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO) {
        User user = new User();
        userRepository.save(user);
        Exhibition ex = ExhibitionConverter.toEntity(user, createExhibitionReqDTO);

        exhibitionRepository.save(ex);
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
    public ExhibitionResDTO.PreviewExhibitionResDTO previewExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO){
        return ExhibitionConverter.toPreviewExhibitionResDTO(createExhibitionReqDTO);
    }

    @Override
    public void deleteExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
        exhibition.delete();
    }
}
