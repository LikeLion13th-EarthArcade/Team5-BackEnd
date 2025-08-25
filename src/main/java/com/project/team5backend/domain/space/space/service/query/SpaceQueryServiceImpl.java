package com.project.team5backend.domain.space.space.service.query;


import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpaceSize;
import com.project.team5backend.domain.space.space.entity.SpaceType;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final EntityManager entityManager;
    private final SpaceConverter spaceConverter;

    // 모든 전시 공간 목록 조회
    @Override
    public List<SpaceResponse.SpaceSearchResponse> getApprovedSpaces() {
        List<Space> approvedSpaces = spaceRepository.findByStatus(Space.Status.APPROVED);
        return spaceConverter.toSpaceSearchResponseList(approvedSpaces);
    }

    //검색 조건에 맞는 전시 공간 목록 조회
    @Override
    public SpaceResponse.SpaceSearchPageResponse searchSpaces(
            LocalDate startDate,
            LocalDate endDate,
            String district,
            SpaceSize size,
            SpaceType type,
            SpaceMood mood,
            int page
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending()); // ✅ id 기준 오름차순 고정

        // Page<Space> 조회
        Page<Space> spaces = spaceRepository.findSpacesWithFilters(
                startDate, endDate, district, size, type, mood, pageable
        );

        // DTO 변환 (static 메서드 사용)
        List<SpaceResponse.SpaceSearchResponse> content =
                spaces.getContent().stream()
                        .map(SpaceConverter::toSearchSpaceResDTO)
                        .toList();

        // PageInfo 생성
        SpaceResponse.SpaceSearchPageResponse.PageInfo pageInfo =
                new SpaceResponse.SpaceSearchPageResponse.PageInfo(
                        spaces.getNumber(),         // 현재 페이지
                        spaces.getSize(),           // 페이지 크기
                        spaces.getTotalElements(),  // 전체 요소 수
                        spaces.getTotalPages(),     // 전체 페이지 수
                        spaces.isFirst(),           // 첫 페이지 여부
                        spaces.isLast()             // 마지막 페이지 여부
                );

        // MapInfo 생성 (서울 시청 기준 좌표 예시)
        SpaceResponse.SpaceSearchPageResponse.MapInfo mapInfo =
                new SpaceResponse.SpaceSearchPageResponse.MapInfo(
                        37.5665,  // latitude
                        126.9780  // longitude
                );

        // 최종 반환
        return new SpaceResponse.SpaceSearchPageResponse(
                content,
                pageInfo,
                mapInfo
        );
    }



    //전시 공간의 상세 정보 조회
    @Override
    public SpaceResponse.SpaceDetailResponse getSpaceDetails(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .filter(s -> s.getStatus() == Space.Status.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("Approved space not found"));
        return spaceConverter.toSpaceDetailResponse(space);
    }
}