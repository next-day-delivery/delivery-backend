package com.nextdaydelivery.delivery.presentation.dto.request;

import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;

public record DeliveryStatusRequest(
        DeliveryStatus status
) {
}
