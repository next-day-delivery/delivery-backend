package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "존재하지 않는 주문입니다."),
    NOT_YOUR_ORDER(HttpStatus.FORBIDDEN, "O002", "본인의 주문 내역만 접근 가능합니다."),
    NOT_YOUR_STORE_ORDER(HttpStatus.FORBIDDEN, "O003", "해당 가게의 주문 권한이 없습니다."),
    CANCEL_TIMEOUT(HttpStatus.BAD_REQUEST, "O004", "주문 후 5분이 경과하여 취소할 수 없습니다."),
    INVALID_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "O005", "변경 불가능한 주문 상태입니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
