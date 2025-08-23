package com.project.team5backend.domain.space.space.converter;

import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.user.entity.User;
import org.springframework.stereotype.Component;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpaceConverter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // SpaceRequest.Create DTO를 Space 엔티티로 변환
    public Space toSpace(SpaceRequest.Create request, User user, String thumbnailFileKey){

        return Space.builder()
                .name(request.name())
                .location(request.location())
                .type(request.type())
                .size(request.size())
                .purpose(request.purpose())
                .mood(request.mood())
                .description(request.description())
                .imageUrls(List.of(thumbnailFileKey))   // ✅ request.images() 대신 사용
                .status(Space.Status.APPROVAL_PENDING)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .user(user)// ✨ User 객체에서 이메일을 가져와 submittedBy 필드에 설정
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
                        space.getSize(),
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