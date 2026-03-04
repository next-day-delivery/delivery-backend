package com.nextdaydelivery.payment.presentation.dto.response;

import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        Integer amount
) {
    public PaymentResponse {
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId는 필수입니다.");
        }
    }
}
