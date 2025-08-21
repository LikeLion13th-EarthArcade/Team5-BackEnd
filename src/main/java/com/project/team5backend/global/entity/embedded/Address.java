package com.project.team5backend.global.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {
    private String city; // 시
    private String district; // 구
    private String neighborhood;  // 동

    @Column(name = "road_address")
    private String roadAddress;// 도로명 (설택)
    private String detail; // 상세주소 (선택)

    private String postalCode; // 우편번호

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;   // 위도 [-90, 90]

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;  // 경도 [-180, 180]
}