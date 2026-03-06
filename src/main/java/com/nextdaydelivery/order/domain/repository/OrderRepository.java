package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findByIdAndOwnerIdWithLock(UUID orderId, Long ownerId);

    Optional<Order> findByIdWithLock(UUID orderId);

    Optional<Order> findByIdAndCustomerIdWithLock(UUID orderId, Long userId);

    Optional<OrderDetails> findByIdWithDetails(UUID orderId);
}
