package com.project.team5backend.domain.space.space.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter

public class SpaceResponse {

    // 검색 결과 DTO
    @Builder
    public record SpaceSearchResponse (
            Long id,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String startDate,
            String endDate,
            String thumbnail
    ){}
    // 페이지 결과 DTO
    public record SpaceSearchPageResponse(
            List<SpaceSearchResponse> items,
            PageInfo pageInfo,
            MapInfo mapInfo
    ) {
        public record PageInfo(
                int number,
                int size,
                long totalElements,
                int totalPages,
                boolean first,
                boolean last
        ) {}

        public record MapInfo(
                Double latitude,
                Double longitude
        ) {}
    }

    // 상세 조회 DTO
    public record SpaceDetailResponse (
            SpaceOverviewDto spaceOverview,
            FacilitiesAndOptionsDto facilitiesAndOptions,
            ContactDto contact
    ){
        public record SpaceOverviewDto (
                String usagePeriod,
                String address,
                BigDecimal latitude,
                BigDecimal longitude,
                String spaceSize,
                String purpose,
                String concept,
                List<String> imageUrls
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