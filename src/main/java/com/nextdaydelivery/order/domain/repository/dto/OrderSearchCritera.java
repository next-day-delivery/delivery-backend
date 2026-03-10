package com.nextdaydelivery.order.domain.repository.dto;

import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderSearchCritera(
        UUID lastReadOrderId,
        Long customerId,
        UUID storeId,
        List<UUID> categoryIds,
        List<OrderStatus> status,
        LocalDate startDate,
        LocalDate endDate
) {
    public static OrderSearchCritera from(OrderSearchRequest request) {
        return new OrderSearchCritera(
                request.lastReadOrderId(),
                request.customerId(),
                request.storeId(),
                request.categoryIds(),
                request.status(),
                request.startDate(),
                request.endDate()
        );
    }
}
