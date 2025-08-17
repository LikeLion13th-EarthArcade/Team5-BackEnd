package com.project.team5backend.domain.exhibition.exhibition.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {
    EXHIBITION_NOT_STARTED(HttpStatus.BAD_REQUEST, "EXHIBITION400_1", "전시가 아직 시작되지 않았습니다."),
    EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_1", "전시를 찾을 수 없습니다."),
    DIFFERENT_EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_2", "서로 다른 4개의 지역구를 가진 전시들을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
