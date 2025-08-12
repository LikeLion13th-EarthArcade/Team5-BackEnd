package com.project.team5backend.domain.Space.service.query;

import com.project.team5backend.domain.Space.dto.response.SpaceResponse;
import com.project.team5backend.domain.Space.dto.request.SpaceRequest;

import java.util.List;


public interface SpaceQueryService {
    List<SpaceResponse.SpaceSearchResponse> getApprovedSpaces();
    List<SpaceResponse.SpaceSearchResponse> searchSpaces(SpaceRequest.Search request);
    SpaceResponse.SpaceDetailResponse getSpaceDetails(Long spaceId);
}