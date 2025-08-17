package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final SpaceConverter spaceConverter;
    private final UserRepository userRepository;

    @Override
    public SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 로그인 여부 체크
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        // 로그인된 사용자의 ID 가져오기
        String submittedBy = authentication.getName();
        User user = userRepository.findByEmailAndIsDeletedFalse(submittedBy)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        // DTO -> 엔티티 변환 (컨버터 활용)
        Space space = spaceConverter.toSpace(request);
        // submittedBy는 로그인 사용자 이메일로 설정
        space.setSubmittedBy(user.getEmail());
        // 저장
        Space savedSpace = spaceRepository.save(space);
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