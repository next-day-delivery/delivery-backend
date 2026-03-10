package com.nextdaydelivery.order.application.handler;

import com.nextdaydelivery.delivery.domain.event.DeliveryCompletedEvent;
import com.nextdaydelivery.order.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeliveryCompletedEvent(DeliveryCompletedEvent event) {
        orderService.completeOrder(event.orderId());
    }
}
