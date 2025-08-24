package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SpaceCommandService {
    SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request, String email, List<MultipartFile> urls);
    void deleteSpace(Long spaceId);
    boolean toggleLike(Long spaceId, Long userId);
    void approveSpace(Long spaceId);
}