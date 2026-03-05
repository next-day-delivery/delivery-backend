package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    public Optional<Order> findByIdAndOwnerId(UUID orderId, Long ownerId);

    public Optional<Order> findById(UUID orderId);
}
