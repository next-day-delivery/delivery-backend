package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.order.presentation.dto.request.ChangeOrderStatusRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public void getOrderList() {

    }

    @Override
    public void getOrderDetail() {

    }

    @Override
    public void cancelOrder() {

    }

    @Override
    public void rejectOrder() {

    }

    @Transactional
    @Override
    public void changeOrderStatus(ChangeOrderStatusRequest request, UUID orderId, Long userId) {
        Order order = orderRepository.findByIdAndOwnerId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디거나 접근 권한이 없습니다."));

        order.changeStatus(request.orderStatus());
    }

    @Transactional
    @Override
    public void changeOrderStatus(ChangeOrderStatusRequest request, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디입니다."));

        order.changeStatus(request.orderStatus());
    }

    @Override
    public void getOrderListActive() {

    }
}
