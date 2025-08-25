package com.project.team5backend.domain.space.space.service.query;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpaceSize;
import com.project.team5backend.domain.space.space.entity.SpaceType;

import java.time.LocalDate;
import java.util.List;


public interface SpaceQueryService {
    List<SpaceResponse.SpaceSearchResponse> getApprovedSpaces();
    SpaceResponse.SpaceSearchPageResponse searchSpaces(
            LocalDate startDate,
            LocalDate endDate,
            String district,
            SpaceSize size,
            SpaceType type,
            SpaceMood mood,
            int page
    );

    SpaceResponse.SpaceDetailResponse getSpaceDetails(Long spaceId);
}