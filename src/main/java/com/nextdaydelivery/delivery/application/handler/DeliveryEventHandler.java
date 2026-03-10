package com.nextdaydelivery.delivery.application.handler;

import com.nextdaydelivery.delivery.application.service.DeliveryService;
import com.nextdaydelivery.order.domain.event.OrderCookedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DeliveryEventHandler {
    private final DeliveryService deliveryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCookedEvent(OrderCookedEvent event) {
        deliveryService.createDelivery(event.orderId());
    }
}
