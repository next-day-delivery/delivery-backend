package com.nextdaydelivery.payment.presentation.dto.request;

import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID cartId,
        @NotNull UUID storeId,
        @NotNull String deliveryAddress,
        @NotNull PaymentMethod paymentMethod
) {
}
