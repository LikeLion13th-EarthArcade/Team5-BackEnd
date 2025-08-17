package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;


public interface SpaceCommandService {
    SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request);
    void deleteSpace(Long spaceId);
    boolean toggleLike(Long spaceId, Long userId);
    void approveSpace(Long spaceId);
}