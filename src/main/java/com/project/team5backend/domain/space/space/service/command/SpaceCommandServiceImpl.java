package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.exhibition.exhibition.repository.SpaceImageRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;

import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final SpaceConverter spaceConverter;
    private final UserRepository userRepository;
    private final RedisImageTracker redisImageTracker;
    private final SpaceImageRepository spaceImageRepository;


    @Override
    public SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 로그인 여부 체크
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        // 1. 로그인된 사용자의 이메일로 User 엔티티를 조회
        String userEmail = authentication.getName();
        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. DTO -> 엔티티 변환 (컨버터에 user 객체 전달)
        Space space = spaceConverter.toSpace(request, user);
        // 저장
        Space savedSpace = spaceRepository.save(space);

        // Redis에서 업로드한 이미지 키 가져오기
        List<String> fileKeys = redisImageTracker.getOrderedFileKeysByEmail(userEmail);

        if (fileKeys.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        // S3 이미지 엔티티로 저장
        for (String fileKey : fileKeys) {
            spaceImageRepository.save(ImageConverter.toEntitySpaceImage(savedSpace, fileKey));
            redisImageTracker.remove(userEmail, fileKey);
        }

        // 엔티티를 응답 DTO로 변환
        return spaceConverter.toSpaceRegistrationResponse(savedSpace);
    }

    @Override
    public boolean toggleLike(Long spaceId, Long userId) {
        // ... 기존 로직과 동일
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 전시 공간이 존재하지 않습니다."));
        return spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId)
                .map(existingLike -> {
                    spaceLikeRepository.delete(existingLike);
                    return false;
                })
                .orElseGet(() -> {
                    SpaceLike like = new SpaceLike();
                    like.setSpace(space);
                    like.setUserId(userId);
                    spaceLikeRepository.save(like);
                    return true;
                });
    }

    @Override
    public void deleteSpace(Long spaceId) {
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public void approveSpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        space.setStatus(Space.Status.APPROVED);
    }
}