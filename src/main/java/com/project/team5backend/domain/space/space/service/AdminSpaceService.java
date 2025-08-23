package com.project.team5backend.domain.space.space.service;


import com.project.team5backend.domain.space.space.dto.request.AdminSpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.AdminSpaceResponse;

import java.util.List;

public interface AdminSpaceService {
    List<AdminSpaceResponse.PendingSpaceListResponse> getPendingSpaces();
    AdminSpaceResponse.AdminActionResponse processSpaceApproval(Long spaceId, AdminSpaceRequest.AdminSpaceReq request);
}
