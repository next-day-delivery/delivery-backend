package com.nextdaydelivery.checkout.presentation.dto.request;

import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutRequest(
        @NotNull UUID cartId,
        @NotNull UUID storeId,
        @NotNull Long amount,
        @NotNull String address,
        @NotNull PaymentMethod paymentMethod
) {
}
