package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
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
    public void cancelOrderByCustomer(UUID orderId, Long userId) {
        Order order = getOrderByCustomerIdWithLock(orderId, userId);
        //5분 이내인지 검증
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Override
    public void cancelOrderByManager(UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Override
    public void rejectOrder(UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(OrderStatus.ORDER_REJECTED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void changeOrderStatusByOwner(ChangeOrderStatusRequest request, UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(request.orderStatus());
    }

    @Transactional
    @Override
    public void changeOrderStatusByManager(ChangeOrderStatusRequest request, UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(request.orderStatus());
    }

    @Override
    public void getOrderListActive() {

    }

    private Order getOrderWithLock(UUID orderId) {
        return orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디입니다."));
    }

    private Order getOrderByCustomerIdWithLock(UUID orderId, Long customerId) {
        return orderRepository.findByIdAndCustomerIdWithLock(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디거나 접근 권한이 없습니다."));
    }

    private Order getOrderByOwnerIdWithLock(UUID orderId, Long ownerId) {
        return orderRepository.findByIdAndOwnerIdWithLock(orderId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디거나 접근 권한이 없습니다."));
    }

    private void validateOrderAccess(Order order) {
    }


}
