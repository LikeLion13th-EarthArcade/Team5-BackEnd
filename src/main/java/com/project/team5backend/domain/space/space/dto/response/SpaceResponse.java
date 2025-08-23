package com.project.team5backend.domain.space.space.dto.response;

import lombok.*;

@Getter
@Setter
public class SpaceResponse {

    // 검색 결과 DTO
    public record SpaceSearchResponse (
            Long id,
            String name,
            String address,
            String startDate,
            String endDate
    ){}

    // 상세 조회 DTO
    public record SpaceDetailResponse (
            SpaceOverviewDto spaceOverview,
            FacilitiesAndOptionsDto facilitiesAndOptions,
            ContactDto contact
    ){
        public record SpaceOverviewDto (
                String usagePeriod,
                String location,
                String operatingHours,
                String spaceSpecs,
                String purpose,
                String concept
        ){}

        public record FacilitiesAndOptionsDto (
                String applicationMethod,
                OtherFacilitiesDto others
        ){}

        public record OtherFacilitiesDto (
                boolean wifi,
                boolean restroom
        ){}

        public record ContactDto (
                String phone,
                String email,
                String website,
                SnsDto sns
        ){}

        public record SnsDto (
                String instagram,
                String facebook
        ){}
    }

    // 등록 응답 DTO
    public record SpaceRegistrationResponse (
            Long spaceId
    ){}
}