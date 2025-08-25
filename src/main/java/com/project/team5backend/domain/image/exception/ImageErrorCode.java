package com.project.team5backend.domain.image.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {
    IMAGE_TOO_MANY_REQUESTS(HttpStatus.BAD_REQUEST, "IMAGE_400_1", "이미지 파일이 5개를 넘었습니다."),
    IMAGE_NOT_FOUND_IN_DTO(HttpStatus.BAD_REQUEST, "IMAGE_404_2", "이미지가 최소 1개 필요합니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_404_1", "S3에 이미지를 찾을 수 없습니다."),
    S3_MOVE_TRASH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_1", "S3 파일 휴지통 이동 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
