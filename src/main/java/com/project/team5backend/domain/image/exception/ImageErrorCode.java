package com.project.team5backend.domain.image.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {
    IMAGE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "IMAGE_400_1", "지원하지 않는 파일 확장자입니다."),
    IMAGE_INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "IMAGE_400_2", "지원하지 않는 콘텐츠 타입입니다."),
    IMAGE_KEY_MISSING(HttpStatus.BAD_REQUEST, "IMAGE_400_3","이미지 키가 누락되었습니다."),
    IMAGE_TOO_MANY_REQUESTS(HttpStatus.BAD_REQUEST, "IMAGE_400_4", "이미지 파일이 5개를 넘었습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_404_1", "S3에 이미지를 찾을 수 없습니다."),
    IMAGE_INVALID_FILE_KEY(HttpStatus.NOT_FOUND, "IMAGE_400_5", "해당 유저의 fileKey가 존재하지 않습니다."),
    IMAGE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "IMAGE_401", "해당 fileKey에 대한 권한이 없습니다."),
    IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_1", "이미지 업로드에 실패했습니다."),
    IMAGE_COMMIT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_2", "이미지 커밋에 실패했습니다."),
    IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_3", "이미지 삭제에 실패했습니다."),
    REDIS_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_4", "Redis에 이미지 추적 정보를 저장하는 데 실패했습니다."),
    REDIS_REMOVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_5", "Redis에서 이미지 추적 정보를 제거하는 데 실패했습니다."),
    REDIS_KEY_FETCH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_6", "Redis에서 이미지 추적 키 목록을 조회하는 데 실패했습니다."),
    REDIS_EXPIRED_FETCH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_7", "Redis에서 만료된 이미지 키를 조회하는 데 실패했습니다."),
    S3_MOVE_TRASH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500_8", "S3 파일 휴지통 이동 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
