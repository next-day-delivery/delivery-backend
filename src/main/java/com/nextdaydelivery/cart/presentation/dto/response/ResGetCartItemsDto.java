package com.nextdaydelivery.cart.presentation.dto.response;

import com.nextdaydelivery.cart.domain.enums.CartStatus;
import java.util.List;
import java.util.UUID;

public record ResGetCartItemsDto(
    UUID cartId,
    UUID storeId,
    CartStatus status,
    List<CartItemDetail> items
) {
    public record CartItemDetail(
        UUID productId,
        String productName,
        Integer price,
        Long quantity
    ) {
    }
}
