package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CartErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "회원을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "상품을 찾을 수 없습니다."),
    ACTIVE_CART_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "활성화된 장바구니가 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "장바구니 품목을 찾을 수 없습니다."),
    INVALID_CART_ITEM_QUANTITY(HttpStatus.BAD_REQUEST, "C005", "장바구니 수량은 0보다 커야 합니다."),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "C006", "장바구니가 비어있습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
