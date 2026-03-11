package com.nextdaydelivery.payment.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentConfirmRequest(
        @NotNull UUID checkoutId,
        @NotNull String orderNo,
        @NotNull String paymentKey,
        @NotNull Long amount
) {
}
