package com.project.team5backend.domain.space.converter;

import com.project.team5backend.domain.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.entity.Space;
import org.springframework.stereotype.Component;
import com.project.team5backend.domain.space.dto.request.SpaceRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpaceConverter {

    // SpaceRequest.Create DTO를 Space 엔티티로 변환
    public Space toSpace(SpaceRequest.Create request) {
        return Space.builder()
                .name(request.name())
                .location(request.location())
                .type(request.type())
                .spec(request.spec())
                .purpose(request.purpose())
                .mood(request.mood())
                .businessRegistrationNumber(request.businessRegistrationNumber())
                .description(request.description())
                .businessRegistrationDocUrl(request.businessRegistrationDocUrl())
                .buildingLedgerDocUrl(request.buildingLedgerDocUrl())
                .imageUrls(request.images())
                .status(Space.Status.APPROVAL_PENDING)
                .startDate(request.startDate() != null ? LocalDate.parse(request.startDate(), DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .endDate(request.endDate() != null ? LocalDate.parse(request.endDate(), DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .build();
    }

    // Space 엔티티를 등록 응답 DTO로 변환
    public SpaceResponse.SpaceRegistrationResponse toSpaceRegistrationResponse(Space space) {
        return new SpaceResponse.SpaceRegistrationResponse(space.getId());
    }

    // Space 엔티티를 검색 결과 DTO로 변환
    public SpaceResponse.SpaceSearchResponse toSpaceSearchResponse(Space space) {
        return new SpaceResponse.SpaceSearchResponse(
                space.getId(),
                space.getName(),
                space.getLocation(),
                space.getStartDate() != null ? space.getStartDate().toString() : null,
                space.getEndDate() != null ? space.getEndDate().toString() : null
        );
    }

    // Space 엔티티 리스트를 검색 결과 DTO 리스트로 변환
    public List<SpaceResponse.SpaceSearchResponse> toSpaceSearchResponseList(List<Space> spaces) {
        return spaces.stream()
                .map(this::toSpaceSearchResponse)
                .collect(Collectors.toList());
    }

    // Space 엔티티를 상세 조회 DTO로 변환
    public SpaceResponse.SpaceDetailResponse toSpaceDetailResponse(Space space) {
        String usagePeriod = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        if (space.getStartDate() != null && space.getEndDate() != null) {
            String startDateStr = space.getStartDate().format(formatter);
            String endDateStr = space.getEndDate().format(formatter);
            usagePeriod = startDateStr + " - " + endDateStr;
        }
        String operatingHours = space.getOperatingHours();
        SpaceResponse.SpaceDetailResponse.SpaceOverviewDto overview =
                new SpaceResponse.SpaceDetailResponse.SpaceOverviewDto(
                        usagePeriod, // 사용 기간
                        operatingHours, //운영 시간
                        space.getLocation(),
                        space.getSpec(),
                        space.getPurpose() != null ? space.getPurpose().name() : null,
                        space.getMood() != null ? space.getMood().name() : null
                );

        // 엔티티에 없는 필드는 null로 처리
        SpaceResponse.SpaceDetailResponse.FacilitiesAndOptionsDto facilities = null;
        SpaceResponse.SpaceDetailResponse.ContactDto contact = null;

        return new SpaceResponse.SpaceDetailResponse(
                overview,
                facilities,
                contact
        );
    }
}