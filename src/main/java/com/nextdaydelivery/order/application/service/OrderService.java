package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderReviewStatusResponse;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface OrderService {

    Slice<OrderListResponse> getOrdersByCustomer(OrderSearchRequest request, int size);

    Slice<OrderListResponse> getOrdersByManager(OrderSearchRequest request, int size);

    Slice<OrderListResponse> getStoreOrders(UUID storeId, OrderSearchRequest request, Long userId, int size);

    OrderDetailResponse getOrderDetail(UUID orderId, Long userId);

    void cancelOrderByCustomer(UUID orderId, Long userId);

    void cancelOrderByManager(UUID orderId);

    void rejectOrder(UUID orderId, Long userId);

    void changeOrderStatusByOwner(OrderStatusRequest request, UUID orderId, Long userId);

    void changeOrderStatusByManager(OrderStatusRequest request, UUID orderId);

    OrderReviewStatusResponse getReviewStatus(UUID orderId);

}
