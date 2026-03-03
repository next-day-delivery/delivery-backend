package com.nextdaydelivery.order.domain.entity;

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
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId; // 주문 PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK (BIGINT)

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 주문 상태 (ENUM)

    @Column(name = "address", columnDefinition = "TEXT")
    private String address; // 배송지 (VARCHAR)

    /**
     * 주문 상태 관리를 위한 ENUM -> 추후 파일 분리
     */
    public enum OrderStatus {
        ORDER_REQUESTED,  // 주문 요청
        ORDER_REJECTED,   // 주문 거절
        ORDER_CANCELED,   // 주문 취소
        ORDER_ACCEPTED,   // 주문 접수
        ORDER_COMPLETED   // 주문 완료
    }
}
