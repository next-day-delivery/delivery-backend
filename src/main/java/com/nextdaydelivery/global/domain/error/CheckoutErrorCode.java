package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CheckoutErrorCode implements ErrorCode {
    CHECKOUT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C001", "해당 결제 시도에 권한이 없습니다."),
    CHECKOUT_CONCURRENCY_ERROR(HttpStatus.CONFLICT, "C002", "동시 요청으로 인해 결제 시도에 실패했습니다. 다시 시도해주세요."),
    INVALID_ORDER_NUMBER(HttpStatus.BAD_REQUEST, "C003", "주문번호가 일치하지 않습니다."),
    INVALID_CHECKOUT_AMOUNT(HttpStatus.BAD_REQUEST, "C004", "결제 금액이 일치하지 않습니다."),
    CHECKOUT_NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "존재하지 않는 결제 시도입니다."),
    CHECKOUT_EXPIRED(HttpStatus.BAD_REQUEST, "C006", "결제 가능 시간이 만료되어 결제할 수 없습니다."),
    INVALID_CHECKOUT_STATUS(HttpStatus.BAD_REQUEST, "C007", "결제 가능 상태가 아닙니다."),
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "C008", "결제 예정 금액이 변경되었습니다. 장바구니를 다시 확인해 주세요."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "C009", "결제 예정 금액이 변경되었습니다. 장바구니를 다시 확인해 주세요.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
