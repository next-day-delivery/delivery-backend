package com.nextdaydelivery.order.presentation.dto.request;

import com.nextdaydelivery.order.domain.enums.OrderStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderSearchRequest(
        UUID lastReadOrderId,
        Long customerId,
        UUID storeId,
        List<UUID> categoryIds,
        List<OrderStatus> status,
        LocalDate startDate,
        LocalDate endDate
) {
}
