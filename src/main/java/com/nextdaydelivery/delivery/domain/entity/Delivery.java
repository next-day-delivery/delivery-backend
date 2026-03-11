package com.nextdaydelivery.delivery.domain.entity;

import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import com.nextdaydelivery.global.domain.entity.BaseAuditEntity;
import com.nextdaydelivery.global.domain.error.DeliveryErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.domain.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "delivery_id", updatable = false, nullable = false)
    private UUID deliveryId; // 배달 PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order; // 배달에서 주문을 참조

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus; // 배달 상태 (PENDING, ING, COMPLETED)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy; // 레코드 삭제자

    @Builder
    public Delivery(Order order, DeliveryStatus status) {
        this.order = order;
        this.deliveryStatus = status;
    }

    public static Delivery createDelivery(Order order) {
        return Delivery.builder()
                .order(order)
                .status(DeliveryStatus.DELIVERY_PENDING)
                .build();
    }

    public void forceUpdateStatus(DeliveryStatus status) {
        this.deliveryStatus = status;
    }

    public void transitionTo(DeliveryStatus status) {
        if (!this.deliveryStatus.canChangeTo(status)) {
            throw new BusinessException(DeliveryErrorCode.INVALID_STATUS_TRANSITION
            );
        }
        this.deliveryStatus = status;
    }
}
