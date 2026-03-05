package com.nextdaydelivery.payment.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentConfirmRequest(
        @NotNull UUID paymentId,
        @NotNull String paymentKey,
        @NotNull Integer amount
) {
}
