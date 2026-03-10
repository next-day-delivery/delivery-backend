package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderReviewStatusResponse;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface OrderService {

    Slice<OrderListResponse> getOrdersByCustomer(OrderSearchRequest request, int size);

    Slice<OrderListResponse> getOrdersByManager(OrderSearchRequest request, int size);

    Slice<OrderListResponse> getStoreOrders(UUID storeId, OrderSearchRequest request, Long userId, int size);

    OrderDetailResponse getOrderDetail(UUID orderId, Long userId, UserRole role);

    Order cancelOrderByCustomer(UUID orderId, Long userId);

    Order cancelOrderByManager(UUID orderId);

    Order rejectOrder(UUID orderId, Long userId);

    void changeOrderStatusByOwner(OrderStatusRequest request, UUID orderId, Long userId);

    void changeOrderStatusByManager(OrderStatusRequest request, UUID orderId);

    OrderReviewStatusResponse getReviewStatus(UUID orderId);

    Order createFromCheckout(Checkout checkout, User user);

}
