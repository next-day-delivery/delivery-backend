package com.nextdaydelivery.payment.domain.entity;

import com.nextdaydelivery.global.baseEntity.BaseEntity;
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
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId; // 결제 PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 결제에서 주문을 참조

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // 결제 방법 (CARD 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태 (CANCEL, COMPLETE, FAILED)

    /**
     * 결제 방법을 위한 ENUM
     */
    public enum PaymentMethod {
        CARD
    }

    /**
     * 결제 상태를 위한 ENUM
     */
    public enum PaymentStatus {
        CANCEL,
        COMPLETE,
        FAILED
    }
}
