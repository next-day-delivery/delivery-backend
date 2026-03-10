package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 존재하는 사용자입니다."),
    INVALID_SIGNUP_ROLE(HttpStatus.BAD_REQUEST, "U002", "허용되지 않은 가입 권한입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "존재하지 않는 유저의 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
