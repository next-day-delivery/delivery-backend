package com.nextdaydelivery.order.domain.event;

import java.util.UUID;

public record OrderCookedEvent(
        UUID orderId
) {
}
