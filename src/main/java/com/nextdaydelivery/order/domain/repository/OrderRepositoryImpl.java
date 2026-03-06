package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJPARepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Order> findByIdAndOwnerIdWithLock(UUID orderId, Long ownerId) {
        return jpaRepository.findByIdAndOwnerIdWithLock(orderId, ownerId);
    }

    @Override
    public Optional<Order> findByIdWithLock(UUID orderId) {
        return jpaRepository.findByIdWithLock(orderId);
    }

    @Override
    public Optional<Order> findByIdAndCustomerIdWithLock(UUID orderId, Long userId) {
        return jpaRepository.findByIdAndCustomerIdWithLock(orderId, userId);
    }


}
