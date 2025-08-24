package com.project.team5backend.domain.space.space.service.query;


import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
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
    public List<SpaceResponse.SpaceSearchResponse> searchSpaces(SpaceRequest.Search request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Space> query = cb.createQuery(Space.class);
        Root<Space> space = query.from(Space.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(space.get("status"), Space.Status.APPROVED));

        if (request.location() != null && !request.location().isEmpty()) {
            predicates.add(cb.like(space.get("location"), "%" + request.location() + "%"));
        }
        if (request.size() != null ) {
            predicates.add(cb.equal(space.get("size"), request.size()));
        }
        if (request.type() != null) {
            predicates.add(cb.equal(space.get("type"), request.type()));
        }
        if (request.mood() != null) {
            predicates.add(cb.equal(space.get("mood"), request.mood()));
        }

        if (request.startDate() != null && !request.startDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(request.startDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            predicates.add(cb.greaterThanOrEqualTo(space.get("endDate"), startDate));
        }
        if (request.endDate() != null && !request.endDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(request.endDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            predicates.add(cb.lessThanOrEqualTo(space.get("startDate"), endDate));
        }

        query.where(predicates.toArray(new Predicate[0]));
        List<Space> resultSpaces = entityManager.createQuery(query).getResultList();

        return spaceConverter.toSpaceSearchResponseList(resultSpaces);
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