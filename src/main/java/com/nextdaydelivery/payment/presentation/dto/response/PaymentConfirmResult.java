package com.nextdaydelivery.payment.presentation.dto.response;

public record PaymentConfirmResult(
        String paymentKey,
        String orderNo,
        Long amount
) {
}
