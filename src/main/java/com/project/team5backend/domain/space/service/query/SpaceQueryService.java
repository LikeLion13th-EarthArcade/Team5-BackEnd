package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.domain.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.dto.request.SpaceRequest;

import java.util.List;


public interface SpaceQueryService {
    List<SpaceResponse.SpaceSearchResponse> getApprovedSpaces();
    List<SpaceResponse.SpaceSearchResponse> searchSpaces(SpaceRequest.Search request);
    SpaceResponse.SpaceDetailResponse getSpaceDetails(Long spaceId);
}