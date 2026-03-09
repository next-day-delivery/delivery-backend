package com.nextdaydelivery.product.exception;

import com.nextdaydelivery.global.domain.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "P001", "해당 ID의 상품은 존재하지 않습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
