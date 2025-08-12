package com.project.team5backend.domain.space.dto.response;

import lombok.*;

public class SpaceResponse {

    // 공통 응답
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonResponse<T> {
        private String status;
        private String message;
        private T data;
    }

    // 검색 결과 DTO
    @Getter
    @Setter
    public static class SpaceSearchResponse {
        private Long id;
        private String name;
        private String address;
        private String startDate;
        private String endDate;
    }

    // 상세 조회 DTO
    @Getter
    @Setter
    public static class SpaceDetailResponse {
        private SpaceOverviewDto spaceOverview;
        private FacilitiesAndOptionsDto facilitiesAndOptions;
        private ContactDto contact;

        @Getter @Setter
        public static class SpaceOverviewDto {
            private String usagePeriod;
            private String location;
            private String operatingHours;
            private String spaceSpecs;
            private String purpose;
            private String concept;
        }

        @Getter @Setter
        public static class FacilitiesAndOptionsDto {
            private String applicationMethod;
            private OtherFacilitiesDto others;
        }

        @Getter @Setter
        public static class OtherFacilitiesDto {
            private boolean wifi;
            private boolean restroom;
        }

        @Getter @Setter
        public static class ContactDto {
            private String phone;
            private String email;
            private String website;
            private SnsDto sns;
        }

        @Getter @Setter
        public static class SnsDto {
            private String instagram;
            private String facebook;
        }
    }

    // 등록 응답 DTO
    @Getter
    @Setter
    public static class SpaceRegistrationResponse {
        private Long spaceId;
    }
}
