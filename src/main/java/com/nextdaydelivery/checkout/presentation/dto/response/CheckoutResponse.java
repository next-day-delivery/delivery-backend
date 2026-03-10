package com.nextdaydelivery.checkout.presentation.dto.response;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CheckoutResponse(
        UUID checkoutId,
        String orderNo,
        UUID cartId,
        Long amount,
        PaymentMethod paymentMethod,
        CheckoutStatus status,
        LocalDateTime getExpiredAt
) {
    public static CheckoutResponse from(Checkout checkout) {
        return CheckoutResponse.builder()
                .checkoutId(checkout.getCheckoutId())
                .amount(checkout.getAmount())
                .cartId(checkout.getCartId())
                .getExpiredAt(checkout.getExpiresAt())
                .status(checkout.getStatus())
                .orderNo(checkout.getOrderNo())
                .paymentMethod(checkout.getPaymentMethod())
                .build();
    }
}
