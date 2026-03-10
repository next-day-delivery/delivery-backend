package com.nextdaydelivery.cart.domain.enums;

// 장바구니 상태를 위한 ENUM
public enum CartStatus {
    ACTIVE("장바구니 초기생성 / 활성화된 장바구니"),
    INACTIVE("비활성화된 장바구니"),
    COMPLETED("주문이 완료된 장바구니")
    ;

    private final String description;

    CartStatus(String description) {
        this.description = description;
    }
}
