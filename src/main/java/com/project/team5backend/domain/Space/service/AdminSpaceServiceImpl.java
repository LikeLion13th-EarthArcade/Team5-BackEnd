package com.project.team5backend.domain.space.service;


import com.project.team5backend.domain.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.space.dto.request.AdminSpaceRequest;
import com.project.team5backend.domain.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSpaceServiceImpl implements AdminSpaceService {

    private final SpaceRepository spaceRepository;
    private final AdminSpaceConverter adminSpaceConverter;


    @Override
    public List<AdminSpaceResponse.PendingSpaceListResponse> getPendingSpaces() {
        List<Space> pendingSpaces = spaceRepository.findByStatus(Space.Status.APPROVAL_PENDING);
        return adminSpaceConverter.toPendingSpaceListResponseList(pendingSpaces);
    }

    @Override
    public AdminSpaceResponse.AdminActionResponse processSpaceApproval(Long spaceId, AdminSpaceRequest.AdminSpaceReq request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));

        if (request.approve()) {
            space.setStatus(Space.Status.APPROVED);
        } else {
            space.setStatus(Space.Status.REJECTED);
        }

        Space updatedSpace = spaceRepository.save(space);
        return adminSpaceConverter.toAdminActionResponse(updatedSpace);
    }
}