package com.nextdaydelivery.order.infrastructure.repository;

import static com.nextdaydelivery.order.domain.entity.QOrder.order;
import static com.nextdaydelivery.order.domain.entity.QOrderLine.orderLine;
import static com.nextdaydelivery.product.domain.entity.QProduct.product;
import static com.nextdaydelivery.store.domain.entity.QStore.store;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderSearchCritera;
import com.nextdaydelivery.order.domain.repository.dto.OrderSlice;
import com.nextdaydelivery.order.domain.repository.dto.QOrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.QOrderLineInfo;
import com.nextdaydelivery.order.domain.repository.dto.QOrderSlice;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

        Map<UUID, OrderDetails> result = queryFactory
                .from(order)
                .leftJoin(orderLine).on(orderLine.order.eq(order))
                .leftJoin(orderLine.product, product)
                .join(order.store, store)
                .where(order.orderId.eq(orderId))
                .transform(
                        groupBy(order.orderId).as(new QOrderDetails(
                                order.orderId,
                                order.user.userId,
                                store.storeId,
                                store.user.userId,
                                store.name,
                                GroupBy.sum(orderLine.price.multiply(orderLine.quantity)),
                                order.orderStatus.stringValue(),
                                order.address,
                                order.createdAt,
                                list(new QOrderLineInfo(
                                        orderLine.orderLineId,
                                        product.productId,
                                        product.productName,
                                        orderLine.price,
                                        orderLine.quantity
                                ))
                        ))
                );
        return Optional.ofNullable(result.get(orderId));
    }

    @Override
    public Slice<OrderSlice> searchOrders(OrderSearchCritera critera, int size) {
        LocalDateTime cursorTime = null;
        if (critera.lastReadOrderId() != null) {
            cursorTime = queryFactory.select(order.createdAt)
                    .from(order)
                    .where(order.orderId.eq(critera.lastReadOrderId()))
                    .fetchOne();
            if (cursorTime == null) {
                throw new BusinessException(OrderErrorCode.INVALID_CURSOR);
            }
        }
        List<UUID> orderIds = queryFactory
                .select(order.orderId)
                .from(order)
                .where(
                        ltOrderId(cursorTime, critera.lastReadOrderId()),
                        customerIdEq(critera.customerId()),
                        storeIdEq(critera.storeId()),
                        statusIn(critera.status()),
                        dateBetween(critera.startDate(), critera.endDate())
                )
                .orderBy(order.createdAt.desc(), order.orderId.desc())
                .limit(size + 1)
                .fetch();
        if (orderIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), PageRequest.ofSize(size), false);
        }

        Map<UUID, OrderSlice> results = queryFactory
                .from(order)
                .leftJoin(orderLine).on(orderLine.order.eq(order))
                .leftJoin(orderLine.product, product)
                .join(order.store, store)
                .where(order.orderId.in(orderIds))
                .orderBy(order.createdAt.desc(), order.orderId.desc())
                .transform(GroupBy.groupBy(order.orderId).as(new QOrderSlice(
                        order.orderId,
                        order.user.userId,
                        store.storeId,
                        store.user.userId,
                        store.name,
                        GroupBy.sum(orderLine.price.multiply(orderLine.quantity)), order.orderStatus.stringValue(),
                        order.address,
                        order.createdAt,
                        GroupBy.list(new QOrderLineInfo(
                                orderLine.orderLineId,
                                product.productId,
                                product.productName,
                                orderLine.price,
                                orderLine.quantity
                        ))

                )));
        List<OrderSlice> content = new ArrayList<>(results.values());
        content.sort((o1, o2) -> {
            int res = o2.createdAt().compareTo(o1.createdAt());
            if (res == 0) {
                return o2.orderId().compareTo(o1.orderId());
            }
            return res;
        });
        boolean hasNext = false;
        if (content.size() > size) {
            content.remove(size);
            hasNext = true;
        }
        return new SliceImpl<>(content, PageRequest.ofSize(size), hasNext);

    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId);
    }

    private BooleanExpression ltOrderId(LocalDateTime lastTime, UUID lastOrderId) {
        if (lastTime == null || lastOrderId == null) {
            return null;
        }
        return order.createdAt.lt(lastTime)
                .or(order.createdAt.eq(lastTime).and(order.orderId.lt(lastOrderId)));
    }

    private BooleanExpression storeIdEq(UUID storeId) {
        return storeId != null ? order.store.storeId.eq(storeId) : null;
    }

    private BooleanExpression statusIn(List<OrderStatus> statuses) {
        return (statuses == null || statuses.isEmpty()) ? null : order.orderStatus.in(statuses);
    }

    private BooleanExpression customerIdEq(Long userId) {
        return userId != null ? order.user.userId.eq(userId) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return null;
        }
        if (start != null && end != null) {
            return order.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }
        if (start != null) {
            return order.createdAt.goe(start.atStartOfDay());
        }
        return order.createdAt.loe(end.atTime(LocalTime.MAX));
    }


}
