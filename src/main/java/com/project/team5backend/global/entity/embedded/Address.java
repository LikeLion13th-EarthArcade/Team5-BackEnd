package com.project.team5backend.global.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String city; // 시
    private String district; // 구
    private String neighborhood;  // 동

    @Column(name = "roadname_address")
    private String roadNameAddress;// 도로명 (설택)
    private String detail; // 상세주소 (선택)

    private String latitude;  // 위도
    private String longitude; // 경도
}
