package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderSlice;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface OrderRepository {

    Optional<Order> findByIdAndOwnerIdWithLock(UUID orderId, Long ownerId);

    Optional<Order> findByIdWithLock(UUID orderId);

    Optional<Order> findByIdAndCustomerIdWithLock(UUID orderId, Long userId);

    Optional<OrderDetails> findByIdWithDetails(UUID orderId);

    Slice<OrderSlice> searchOrders(OrderSearchRequest request, int size);
}
