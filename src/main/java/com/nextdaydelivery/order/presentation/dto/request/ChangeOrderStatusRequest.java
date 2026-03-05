package com.nextdaydelivery.order.presentation.dto.request;

import com.nextdaydelivery.order.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(
        @NotNull OrderStatus orderStatus
) {
}
