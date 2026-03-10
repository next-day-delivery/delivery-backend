package com.nextdaydelivery.cart.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReqPostCartItemDto(
    @NotNull UUID productId,
    @NotNull @Min(1) Long quantity
) {
}
