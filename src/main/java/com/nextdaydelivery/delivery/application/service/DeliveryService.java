package com.nextdaydelivery.delivery.application.service;

import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import com.nextdaydelivery.order.domain.entity.Order;
import java.util.UUID;

public interface DeliveryService {
    UUID createDelivery(Order order);

    void updateDeliveryStatusByOwner(UUID deliveryId, DeliveryStatus request, Long userId);

    void updateDeliveryStatusByManager(UUID deliveryId, DeliveryStatus request);
}
