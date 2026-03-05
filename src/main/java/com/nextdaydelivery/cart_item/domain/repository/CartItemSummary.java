package com.nextdaydelivery.cart_item.domain.repository;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;

public record CartItemSummary(
    UUID cartId,
    UUID storeId,
    UUID productId,
    String productName,
    Integer price,
    Long quantity
) {
    @QueryProjection
    public CartItemSummary(
        UUID cartId,
        UUID storeId,
        UUID productId,
        String productName,
        Integer price,
        Long quantity
    ) {
        this.cartId = cartId;
        this.storeId = storeId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
