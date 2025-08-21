package com.project.team5backend.global.address.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AddressErrorCode implements BaseErrorCode {
    ADDRESS_INVALID_INPUT(HttpStatus.BAD_REQUEST, "ADDRESS_400_1", "유효한 주소/좌표 입력이 필요합니다."),
    ADDRESS_GEOCODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ADDRESS_400_2","좌표 변환 결과가 없습니다."),
    ADDRESS_OUT_OF_REGION(HttpStatus.BAD_REQUEST, "ADDRESS_400_3", "현재는 서울 지역만 지원합니다."),
    MISSING_ADDRESS(HttpStatus.NOT_FOUND, "ADDRESS_404_1", "카카오 rest key가 없습니다."),
    ADDRESS_KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "ADDRESS_502_1","카카오 로컬 API 호출 실패");
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}