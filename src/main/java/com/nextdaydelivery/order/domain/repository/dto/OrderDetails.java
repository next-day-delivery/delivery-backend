package com.nextdaydelivery.order.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import java.util.UUID;

//결제 배달 추가 예정
public record OrderDetails(
        UUID orderId,
        Long customerId,
        UUID storeId,
        Long ownerId,
        String storeName,
        Long totalPrice,
        String orderStatus,
        String orderAddress,
        List<OrderLineInfo> orderLines
) {
    @QueryProjection
    public OrderDetails(
            UUID orderId,
            Long customerId,
            UUID storeId,
            Long ownerId,
            String storeName,
            Long totalPrice,
            String orderStatus,
            String orderAddress,
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
        this.orderLines = orderLines;
    }
}

