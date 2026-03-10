package com.nextdaydelivery.delivery.application.service;

import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import java.util.UUID;

public interface DeliveryService {
    void createDelivery(UUID orderId);

    void updateDeliveryStatusByOwner(UUID deliveryId, DeliveryStatus request, Long userId);

    void updateDeliveryStatusByManager(UUID deliveryId, DeliveryStatus request);
}
