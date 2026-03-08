package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.global.config.PaginationConfig;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderSlice;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaginationConfig paginationConfig;

    @Override
    public Slice<OrderListResponse> getOrdersByCustomer(OrderSearchRequest request, int size) {
        //유효한 유저인지 검증
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(request, validatedPageSize);
        return orderSlices.map(slice -> OrderListResponse.ofCustomer(slice, request.customerId()));
    }

    @Override
    public Slice<OrderListResponse> getOrdersByManager(OrderSearchRequest request, int size) {
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(request, validatedPageSize);
        return orderSlices.map(OrderListResponse::from);
    }

    @Override
    public Slice<OrderListResponse> getStoreOrders(UUID storeId, OrderSearchRequest request, Long userId, int size) {
        //유저가 해당 가게 사장인지 검증
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(request, validatedPageSize);
        return orderSlices.map(OrderListResponse::from);

    }

    @Override
    public OrderDetailResponse getOrderDetail(UUID orderId, Long userId) {
        OrderDetails details = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이디입니다."));
        validateOrderAccess(details);
        return OrderDetailResponse.from(details, userId);
    }

    @Transactional
    @Override
    public void cancelOrderByCustomer(UUID orderId, Long userId) {
        Order order = getOrderByCustomerIdWithLock(orderId, userId);
        //5분 이내인지 검증
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void cancelOrderByManager(UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void rejectOrder(UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(OrderStatus.ORDER_REJECTED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void changeOrderStatusByOwner(OrderStatusRequest request, UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(request.orderStatus());
    }

    @Transactional
    @Override
    public void changeOrderStatusByManager(OrderStatusRequest request, UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(request.orderStatus());
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

    private void validateOrderAccess(OrderDetails details) {
        //권한 검증 - 유저는 자기 주문인지,
    }


}
