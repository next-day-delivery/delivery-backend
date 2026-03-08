package com.nextdaydelivery.order.presentation.dto.response;

import com.nextdaydelivery.order.domain.repository.dto.OrderLineInfo;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderLineSummary(
        UUID orderLineId,
        UUID productId,
        String productName,
        Long quantity
) {
    public static OrderLineSummary from(OrderLineInfo info) {
        return OrderLineSummary.builder()
                .orderLineId(info.orderLineId())
                .productId(info.productId())
                .productName(info.productName())
                .quantity(info.quantity())
                .build();
    }
}
