package com.nextdaydelivery.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
