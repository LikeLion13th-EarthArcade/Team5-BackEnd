package com.project.team5backend.domain.Space.service.command;


import com.project.team5backend.domain.Space.dto.request.SpaceRequest;
import com.project.team5backend.domain.Space.dto.response.SpaceResponse;


public interface SpaceCommandService {
    SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request);
    void deleteSpace(Long spaceId);
    boolean toggleLike(Long spaceId, Long userId);
    void approveSpace(Long spaceId);
}