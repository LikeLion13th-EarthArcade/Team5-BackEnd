package com.project.team5backend.domain.exhibition.exhibition.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {
    EXHIBITION_NOT_STARTED(HttpStatus.BAD_REQUEST, "EXHIBITION400_1", "전시가 아직 시작되지 않았습니다."),
    NOT_PENDING_EXHIBITION(HttpStatus.BAD_REQUEST, "EXHIBITION400_2", "전시 상태가 PENDING이 아닙니다."),
    PENDING_EXHIBITION(HttpStatus.BAD_REQUEST, "EXHIBITION400_3", "전시 상태가 PENDING 입니다."),
    NOT_APPROVE_EXHIBITION(HttpStatus.BAD_REQUEST, "EXHIBITION400_4", "전시 상태가 Approve가 아닙니다."),
    DELETED_EXHIBITION(HttpStatus.BAD_REQUEST, "EXHIBITION400_5", "삭제된 전시입니다."),
    EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_1", "전시를 찾을 수 없습니다."),
    EXHIBITION_FORBIDDEN(HttpStatus.FORBIDDEN, "EXHIBITION403_1", "해당 전시에 대한 권한이 없습니다."),
    PENDING_EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_2", "STATUS가 PENDING인 전시를 찾을 수 없습니다."),
    DIFFERENT_EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_3", "서로 다른 4개의 지역구를 가진 전시들을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
