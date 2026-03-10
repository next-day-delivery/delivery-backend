package com.nextdaydelivery.order.application.fascade;

import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.payment.application.service.PaymentService;
import com.nextdaydelivery.user.application.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancelFacade {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserService userService;

    @Transactional
    public void cancelOrderByCustomer(UUID orderId, Long userId) {
        Order order = orderService.cancelOrderByCustomer(orderId, userId);
        paymentService.cancelPayment(order);
    }

    @Transactional
    public void cancelOrderByManager(UUID orderId) {
        Order order = orderService.cancelOrderByManager(orderId);
        paymentService.cancelPayment(order);
    }

    @Transactional
    public void rejectOrder(UUID orderId, Long userId) {
        Order order = orderService.rejectOrder(orderId, userId);
        paymentService.cancelPayment(order);
    }
}
