package com.project.team5backend.domain.space.service;


import com.project.team5backend.domain.space.dto.request.AdminSpaceRequest;
import com.project.team5backend.domain.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSpaceServiceImpl implements AdminSpaceService {

    private final SpaceRepository spaceRepository;

    @Override
    public List<AdminSpaceResponse.PendingSpaceListResponse> getPendingSpaces() {
        // Space.Status.APPROVAL_PENDING 상태인 공간만 조회
        List<Space> pendingSpaces = spaceRepository.findByStatus(Space.Status.APPROVAL_PENDING);
        return pendingSpaces.stream()
                .map(space -> {
                    AdminSpaceResponse.PendingSpaceListResponse response = new AdminSpaceResponse.PendingSpaceListResponse();
                    response.setSpaceId(space.getId());
                    response.setSpaceName(space.getName());
                    // 제출자 이메일, 제출 시각 등은 Space 엔티티에 관련 필드가 있어야 합니다.
                    // response.setSubmittedBy(space.getUser().getEmail());
                    // response.setSubmittedAt(space.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminSpaceResponse.AdminActionResponse processSpaceApproval(Long spaceId, AdminSpaceRequest request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));

        if (request.isApprove()) {
            space.setStatus(Space.Status.APPROVED);
        } else {
            space.setStatus(Space.Status.REJECTED);
            // 거절 사유를 저장하는 로직을 추가할 수 있습니다.
            // space.setRejectionReason(request.getReason());
        }

        Space updatedSpace = spaceRepository.save(space);

        AdminSpaceResponse.AdminActionResponse response = new AdminSpaceResponse.AdminActionResponse();
        response.setId(updatedSpace.getId());
        response.setStatus(updatedSpace.getStatus().name());
        return response;
    }
}