package com.nextdaydelivery.payment.presentation.dto.response;

import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentConfirmResponse(
        UUID orderId,
        String orderNumber,
        CheckoutStatus checkoutStatus,
        PaymentStatus paymentStatus
) {
    public static PaymentConfirmResponse success(
            UUID orderId,
            String orderNumber,
            CheckoutStatus checkoutStatus,
            PaymentStatus paymentStatus
    ) {
        return PaymentConfirmResponse.builder()
                .orderId(orderId)
                .orderNumber(orderNumber)
                .checkoutStatus(checkoutStatus)
                .paymentStatus(paymentStatus)
                .build();
    }
}
