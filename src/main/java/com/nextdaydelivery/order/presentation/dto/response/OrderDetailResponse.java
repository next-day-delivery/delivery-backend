package com.nextdaydelivery.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderLineInfo;
import java.util.List;
import java.util.UUID;

//배달, 결제정보 추가 예정
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
        List<OrderLineInfo> orderLines
) {
    public static OrderDetailResponse from(OrderDetails details, Long userId) {
        Long filteredOwnerId = (userId == details.customerId()) ? null : details.ownerId();

        return new OrderDetailResponse(
                details.orderId(),
                details.customerId(),
                details.storeId(),
                filteredOwnerId,
                details.storeName(),
                details.totalPrice(),
                details.orderStatus(),
                details.orderAddress(),
                details.orderLines()
        );
    }
}
