package com.nextdaydelivery.order.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;

public record OrderLineInfo(
        UUID orderLineId,
        UUID productId,
        String productName,
        Long price,
        Long quantity
) {
    @QueryProjection
    public OrderLineInfo(
            UUID orderLineId,
            UUID productId,
            String productName,
            Long price,
            Long quantity
    ) {
        this.orderLineId = orderLineId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
