package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.order.presentation.dto.request.ChangeOrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import java.util.UUID;

public interface OrderService {

    void getOrderList();

    OrderDetailResponse getOrderDetail(UUID orderId, Long userId);

    void cancelOrderByCustomer(UUID orderId, Long userId);

    void cancelOrderByManager(UUID orderId);

    void rejectOrder(UUID orderId, Long userId);

    void changeOrderStatusByOwner(ChangeOrderStatusRequest request, UUID orderId, Long userId);

    void changeOrderStatusByManager(ChangeOrderStatusRequest request, UUID orderId);

    void getOrderListActive();
}
