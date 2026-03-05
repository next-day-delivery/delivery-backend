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
    public Optional<Order> findByIdAndOwnerId(UUID orderId, Long ownerId) {
        return jpaRepository.findByIdAndOwnerId(orderId, ownerId);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId);
    }
}
