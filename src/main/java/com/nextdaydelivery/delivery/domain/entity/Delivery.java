package com.nextdaydelivery.delivery.domain.entity;

import com.nextdaydelivery._domainName_sample.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "delivery_id", updatable = false, nullable = false)
    private UUID deliveryId; // 배달 PK

    @Column(name = "order_id", nullable = false)
    private UUID orderId; // 주문 PK

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus; // 배달 상태 (PENDING, ING, COMPLETED)

    /**
     * 배달 상태 관리를 위한 ENUM
     */
    public enum DeliveryStatus {
        DELIVERY_PENDING,   // 배달 대기
        DELIVERY_ING,       // 배달 중
        DELIVERY_COMPLETED  // 배달 완료
    }
}
