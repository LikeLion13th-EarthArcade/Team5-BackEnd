package com.project.team5backend.global.address.dto.response;

import lombok.Builder;

import java.util.List;

public class AddressResDTO {
    @Builder
    public record AddressCreateResDTO(
            String city,          // 시/도
            String district,      // 구
            String neighborhood,  // 동/로/가
            String roadNameAddress,
            String detail,
            String postalCode,
            Double latitude,      // 위도(y)
            Double longitude      // 경도(x)
    ) {}

    public record KakaoAddressResDTO(List<Document> documents) {
        public record Document(
                String x, String y,
                RoadAddress road_address,
                Address address
        ) {}

        public record RoadAddress(
                String address_name,
                String region_1depth_name, // 시/도
                String region_2depth_name, // 시/군/구
                String region_3depth_name  // 동/로/가
        ) {}

        public record Address(
                String address_name,
                String region_1depth_name,
                String region_2depth_name,
                String region_3depth_name
        ) {}
    }
}
