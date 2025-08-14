package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request) {
        // DTO를 엔티티로 변환
        Space space = spaceConverter.toSpace(request);
        // 로그인된 사용자의 ID를 가져와 submittedBy 필드에 저장
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String submittedBy = authentication.getName();
            space.setSubmittedBy(submittedBy);
        }

        Space savedSpace = spaceRepository.save(space);

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