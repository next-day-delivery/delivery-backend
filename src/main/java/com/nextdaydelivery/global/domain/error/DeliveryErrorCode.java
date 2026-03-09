package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {
    DELIVERY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "O001", "접근 권한이 없는 배달입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "0002", "변경할 수 없는 상태입니다."),
    DELIVERY_NOT_FOUND(HttpStatus.BAD_REQUEST, "0003", "존재하지 않는 배송아이디입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
