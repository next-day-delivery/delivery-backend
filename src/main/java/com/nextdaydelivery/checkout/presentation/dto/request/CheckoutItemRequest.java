package com.nextdaydelivery.checkout.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutItemRequest(
        @NotNull UUID productId,
        @NotBlank String productName,
        @Min(1) int quantity,
        @NotNull Long price
) {
}
