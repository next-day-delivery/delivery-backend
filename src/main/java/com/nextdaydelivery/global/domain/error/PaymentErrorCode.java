package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_INVALID_ORDER_NUMBER(HttpStatus.BAD_REQUEST, "P001", "주문번호가 일치하지 않습니다."),
    PAYMENT_INVALID_CHECKOUT_AMOUNT(HttpStatus.BAD_REQUEST, "P002", "결제 금액이 일치하지 않습니다."),
    PAYMENT_PAID_ALREADY(HttpStatus.BAD_REQUEST, "P003", "결제 처리가 이미 완료 되었습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "P004", "결제 처리가 이미 진행되고 있습니다."),
    PAYMENT_CONCURRENCY_ERROR(HttpStatus.BAD_REQUEST, "P005", "결제 처리가 동시에 요청되어 진행할 수 없습니다."),
    PAYMENT_INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "P006", "결제 키가 일치하지 않습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P007", "결제 내역이 존재 하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
