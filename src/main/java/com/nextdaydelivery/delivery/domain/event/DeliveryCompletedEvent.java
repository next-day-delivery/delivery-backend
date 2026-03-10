package com.nextdaydelivery.delivery.domain.event;

import java.util.UUID;

public record DeliveryCompletedEvent(
        UUID orderId
) {
}
