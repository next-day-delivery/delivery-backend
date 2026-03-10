package com.nextdaydelivery.order.domain.entity;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.global.domain.entity.CreatedAuditEntity;
import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.application.dto.OrderSnapshot;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends CreatedAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId; // 주문 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 주문 상태 (ENUM)

    @Column(name = "address")
    private String address; // 배송지 (VARCHAR)

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public void changeStatus(OrderStatus status) {
        if (!this.orderStatus.canChangeTo(status)) {
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_CHANGE);
        }
        this.orderStatus = status;
    }

    public void validateCancelableTime() {
        if (LocalDateTime.now().isAfter(this.getCreatedAt().plusMinutes(5))) {
            throw new BusinessException(OrderErrorCode.CANCEL_TIMEOUT);
        }
    }

    public void markAsReviewed() {
        this.reviewedAt = LocalDateTime.now();
    }

    public boolean isReviewed() {
        return this.reviewedAt != null;
    }

    @Builder
    public Order(String address, String orderNo, User user, Store store) {
        this.orderStatus = OrderStatus.ORDER_REQUESTED;
        this.user = user;
        this.address = address;
        this.store = store;
        this.orderNo = orderNo;

    }

    public static Order from(Checkout checkout, OrderSnapshot snapshot, User user, Store store) {
        return Order.builder()
                .orderNo(checkout.getOrderNo())
                .address(snapshot.address())
                .user(user)
                .store(store)
                .build();
    }
}
