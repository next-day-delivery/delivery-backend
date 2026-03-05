package com.nextdaydelivery.cart.presentation.dto.response;

import com.nextdaydelivery.cart.domain.enums.CartStatus;
import java.util.UUID;

public record ResPatchCartItemDto(
    UUID cartId,
    UUID storeId,
    UUID productId,
    Long quantity,
    CartStatus cartStatus
) {
}
