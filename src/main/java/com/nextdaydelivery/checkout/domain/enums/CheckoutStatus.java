package com.nextdaydelivery.checkout.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CheckoutStatus {
    PAYMENT_PENDING("결제창 진입하여 결제 대기 상태"),
    PAID("결제 완료"),
    EXPIRED("유효 시간 만료");

    private final String description;


}
