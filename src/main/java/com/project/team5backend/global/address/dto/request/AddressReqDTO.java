package com.project.team5backend.global.address.dto.request;

import jakarta.validation.constraints.NotBlank;


public class AddressReqDTO {
    public record AddressCreateReqDTO(
            @NotBlank(message = "도로명 주소 입력")
            String roadAddress,
            String jibunAddress,
            String postalCode,
            String detail
    ) {}
}
