package com.nextdaydelivery.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderLineInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

//배달, 결제정보 추가 예정
@Builder
public record OrderDetailResponse(
        UUID orderId,
        Long customerId,
        UUID storeId,
        @JsonInclude(Include.NON_NULL)
        Long ownerId,
        String storeName,
        Long totalPrice,
        String orderStatus,
        String orderAddress,
        LocalDateTime createdAt,
        List<OrderLineInfo> orderLines
) {
    public static OrderDetailResponse from(OrderDetails details, Long userId) {
        Long filteredOwnerId = userId.equals(details.customerId()) ? null : details.ownerId();

        return OrderDetailResponse.builder()
                .orderId(details.orderId())
                .customerId(details.customerId())
                .storeId(details.storeId())
                .ownerId(filteredOwnerId)
                .totalPrice(details.totalPrice())
                .storeName(details.storeName())
                .orderStatus(details.orderStatus())
                .createdAt(details.createdAt())
                .orderLines(details.orderLines())
                .build();
    }
}
