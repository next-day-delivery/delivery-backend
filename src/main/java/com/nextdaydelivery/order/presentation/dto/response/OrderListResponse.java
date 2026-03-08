package com.nextdaydelivery.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nextdaydelivery.order.domain.repository.dto.OrderSlice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderListResponse(
        UUID orderId,
        Long customerId,
        UUID storeId,
        @JsonInclude(Include.NON_NULL)
        Long ownerId,
        String storeName,
        Long totalPrice,
        String orderStatus,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:MM:SS", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        List<OrderLineSummary> orderLines
) {
    public static OrderListResponse ofCustomer(OrderSlice slice, Long viewerId) {
        Long filteredOwnerId = (viewerId.equals(slice.ownerId())) ? slice.ownerId() : null;

        return OrderListResponse.builder()
                .orderId(slice.orderId())
                .customerId(slice.customerId())
                .storeId(slice.storeId())
                .ownerId(filteredOwnerId)
                .storeName(slice.storeName())
                .orderStatus(slice.orderStatus())
                .createdAt(slice.createdAt())
                .orderLines(slice.orderLines().stream().map(OrderLineSummary::from).toList())
                .build();

    }

    public static OrderListResponse from(OrderSlice slice) {

        return OrderListResponse.builder()
                .orderId(slice.orderId())
                .customerId(slice.customerId())
                .storeId(slice.storeId())
                .ownerId(slice.ownerId())
                .storeName(slice.storeName())
                .orderStatus(slice.orderStatus())
                .createdAt(slice.createdAt())
                .orderLines(slice.orderLines().stream().map(OrderLineSummary::from).toList())
                .build();

    }

}
