package com.project.team5backend.domain.Space.service.command;


import com.project.team5backend.domain.Space.dto.request.SpaceRequest;
import com.project.team5backend.domain.Space.dto.response.SpaceResponse;
import com.project.team5backend.domain.Space.entity.Space;
import com.project.team5backend.domain.Space.entity.SpaceLike;
import com.project.team5backend.domain.Space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.Space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final SpaceLikeRepository spaceLikeRepository;

    @Override
    public SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request) {
        Space space = new Space();
        space.setName(request.getName());
        space.setLocation(request.getLocation());
        space.setType(request.getType());
        space.setSpec(request.getSpec());
        space.setPurpose(request.getPurpose());
        space.setMood(request.getMood());
        space.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        space.setDescription(request.getDescription());
        space.setBusinessRegistrationDocUrl(request.getBusinessRegistrationDocUrl());
        space.setBuildingLedgerDocUrl(request.getBuildingLedgerDocUrl());
        space.setImageUrls(request.getImages());
        space.setStatus(Space.Status.APPROVAL_PENDING);

        Space savedSpace = spaceRepository.save(space);

        SpaceResponse.SpaceRegistrationResponse response = new SpaceResponse.SpaceRegistrationResponse();
        response.setSpaceId(savedSpace.getId());
        return response;
    }
    @Transactional
    @Override
    public boolean toggleLike(Long spaceId, Long userId) {
        // 1. 해당 공간이 존재하는지 확인
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 전시 공간이 존재하지 않습니다."));

        // 2. 기존 좋아요 여부 확인
        return spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId)
                .map(existingLike -> {
                    // 이미 좋아요 → 취소
                    spaceLikeRepository.delete(existingLike);
                    return false;
                })
                .orElseGet(() -> {
                    // 좋아요 추가
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
        spaceRepository.save(space);
    }
}