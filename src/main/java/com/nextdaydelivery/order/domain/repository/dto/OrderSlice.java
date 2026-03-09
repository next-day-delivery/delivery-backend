package com.nextdaydelivery.order.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderSlice(
        UUID orderId,
        Long customerId,
        UUID storeId,
        Long ownerId,
        String storeName,
        Long totalPrice,
        String orderStatus,
        String orderAddress,
        LocalDateTime createdAt,
        List<OrderLineInfo> orderLines
) {
    @QueryProjection
    public OrderSlice(
            UUID orderId,
            Long customerId,
            UUID storeId,
            Long ownerId,
            String storeName,
            Long totalPrice,
            String orderStatus,
            String orderAddress,
            LocalDateTime createdAt,
            List<OrderLineInfo> orderLines

    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.ownerId = ownerId;
        this.storeName = storeName;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderAddress = orderAddress;
        this.createdAt = createdAt;
        this.orderLines = orderLines;
    }
}
