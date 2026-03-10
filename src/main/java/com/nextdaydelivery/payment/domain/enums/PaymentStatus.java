package com.nextdaydelivery.payment.domain.enums;

import lombok.RequiredArgsConstructor;

/**
 * 결제 상태를 위한 ENUM
 */
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("결제 대기"),
    CANCELED("결제 취소"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELFAILED("결제 취소 실패");

    private final String description;
}