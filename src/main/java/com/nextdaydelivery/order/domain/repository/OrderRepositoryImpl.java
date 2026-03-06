package com.nextdaydelivery.order.domain.repository;

import static com.nextdaydelivery.order.domain.entity.QOrder.order;
import static com.nextdaydelivery.order.domain.entity.QOrderLine.orderLine;
import static com.nextdaydelivery.product.domain.entity.QProduct.product;
import static com.nextdaydelivery.store.domain.entity.QStore.store;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.QOrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.QOrderLineInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Map;
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

    @Override
    public Optional<OrderDetails> findByIdWithDetails(UUID orderId) {

        Map<UUID, OrderDetails> result = queryFactory.
                from(orderLine)
                .innerJoin(orderLine.order, order)
                .innerJoin(order.store, store)
                .innerJoin(orderLine.product, product)
                .where(
                        order.orderId.eq(orderId)
                )
                .transform(
                        groupBy(order.orderId).as(new QOrderDetails(
                                order.orderId,
                                order.user.userId,
                                store.storeId,
                                store.user.userId,
                                store.name,
                                orderLine.price.multiply(orderLine.quantity).sumLong(),
                                order.orderStatus.stringValue(),
                                order.address,
                                list(new QOrderLineInfo(
                                        orderLine.orderLineId,
                                        orderLine.product.productId,
                                        orderLine.product.productName,
                                        orderLine.price,
                                        orderLine.quantity
                                ))
                        ))
                );
        return Optional.ofNullable(result.get(orderId));
    }


}
