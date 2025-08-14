package com.project.team5backend.domain.Space.service.query;


import com.project.team5backend.domain.Space.dto.response.SpaceResponse;
import com.project.team5backend.domain.Space.dto.request.SpaceRequest;
import com.project.team5backend.domain.Space.entity.Space;
import com.project.team5backend.domain.Space.repository.SpaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final EntityManager entityManager;

    @Override
    public List<SpaceResponse.SpaceSearchResponse> getApprovedSpaces() {
        List<Space> approvedSpaces = spaceRepository.findByStatus(Space.Status.APPROVED);
        return approvedSpaces.stream()
                .map(this::toSpaceSearchResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceResponse.SpaceSearchResponse> searchSpaces(SpaceRequest.Search request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Space> query = cb.createQuery(Space.class);
        Root<Space> space = query.from(Space.class);

        List<Predicate> predicates = new ArrayList<>();

        // 상태가 'APPROVED'인 공간만 조회
        predicates.add(cb.equal(space.get("status"), Space.Status.APPROVED));

        // 검색 조건
        if (request.getRegion() != null && !request.getRegion().isEmpty()) {
            predicates.add(cb.like(space.get("location"), "%" + request.getRegion() + "%"));
        }
        if (request.getSize() != null && !request.getSize().isEmpty()) {
            predicates.add(cb.like(space.get("spec"), "%" + request.getSize() + "%"));
        }
        if (request.getType() != null) {
            predicates.add(cb.equal(space.get("type"), request.getType()));
        }
        if (request.getMood() != null) {
            predicates.add(cb.equal(space.get("mood"), request.getMood()));
        }
        // TODO: startDate, endDate 검색 로직 추가

        query.where(predicates.toArray(new Predicate[0]));

        List<Space> resultSpaces = entityManager.createQuery(query).getResultList();

        return resultSpaces.stream()
                .map(this::toSpaceSearchResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SpaceResponse.SpaceDetailResponse getSpaceDetails(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .filter(s -> s.getStatus() == Space.Status.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("Approved space not found"));
        return toSpaceDetailResponse(space);
    }

    private SpaceResponse.SpaceSearchResponse toSpaceSearchResponse(Space space) {
        SpaceResponse.SpaceSearchResponse response = new SpaceResponse.SpaceSearchResponse();
        response.setId(space.getId());
        response.setName(space.getName());
        response.setAddress(space.getLocation());
        // TODO: startDate, endDate 필드 매핑 로직 추가
        return response;
    }

    private SpaceResponse.SpaceDetailResponse toSpaceDetailResponse(Space space) {
        SpaceResponse.SpaceDetailResponse response = new SpaceResponse.SpaceDetailResponse();

        // Space 엔티티 데이터를 SpaceDetailResponse DTO에 매핑
        SpaceResponse.SpaceDetailResponse.SpaceOverviewDto overview = new SpaceResponse.SpaceDetailResponse.SpaceOverviewDto();
        overview.setUsagePeriod("TODO: 기간 설정");
        overview.setLocation(space.getLocation());
        overview.setPurpose(space.getPurpose().toString()); // Enum을 String으로 변환
        overview.setConcept(space.getMood().toString()); // Enum을 String으로 변환
        response.setSpaceOverview(overview);

        // TODO: FacilitiesAndOptionsDto, ContactDto 등 나머지 필드 매핑 로직 추가

        return response;
    }
}