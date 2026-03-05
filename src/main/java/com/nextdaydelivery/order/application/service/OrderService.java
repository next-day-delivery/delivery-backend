package com.nextdaydelivery.order.application.service;

import com.nextdaydelivery.order.presentation.dto.request.ChangeOrderStatusRequest;
import java.util.UUID;

public interface OrderService {

    public void getOrderList();

    public void getOrderDetail();

    public void cancelOrder();

    public void rejectOrder();

    public void changeOrderStatus(ChangeOrderStatusRequest request, UUID orderId, Long userId);

    public void changeOrderStatus(ChangeOrderStatusRequest request, UUID orderId);

    public void getOrderListActive();
}
