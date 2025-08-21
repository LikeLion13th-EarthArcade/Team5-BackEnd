package com.project.team5backend.global.address.converter;

import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.entity.embedded.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressConverter {
    public static Address toAddress(AddressResDTO.AddressCreateResDTO dto) {
        BigDecimal lat = dto.latitude() != null ? BigDecimal.valueOf(dto.latitude()) : null;
        BigDecimal lng = dto.longitude() != null ? BigDecimal.valueOf(dto.longitude()) : null;

        return new Address(
                dto.city(),
                dto.district(),
                dto.neighborhood(),
                dto.roadNameAddress(),
                dto.detail(),
                dto.postalCode(),
                lat,  // ← 숫자 타입
                lng   // ← 숫자 타입
        );
    }

    public static AddressResDTO.AddressCreateResDTO toCreateAddressResDTO(String city, String district,String neighborhood, String roadName,
                                                                          String detail, String postalCode, Double latitude, Double longitude) {
        return AddressResDTO.AddressCreateResDTO.builder()
                .city(city)
                .district(district)
                .neighborhood(neighborhood)
                .roadNameAddress(roadName)
                .detail(detail)
                .postalCode(postalCode)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}