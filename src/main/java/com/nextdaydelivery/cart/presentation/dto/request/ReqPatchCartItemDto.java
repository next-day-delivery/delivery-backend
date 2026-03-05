package com.nextdaydelivery.cart.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReqPatchCartItemDto(
    @NotNull @Min(1) Long quantity
) {
}
