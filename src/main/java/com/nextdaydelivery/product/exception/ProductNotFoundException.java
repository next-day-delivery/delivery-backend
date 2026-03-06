package com.nextdaydelivery.product.exception;

public class ProductNotFoundException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 상품의 UUID 입니다.";

    public ProductNotFoundException() {
        super(MESSAGE);
    }
}
