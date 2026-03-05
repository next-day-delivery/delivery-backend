package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 존재하는 사용자입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
