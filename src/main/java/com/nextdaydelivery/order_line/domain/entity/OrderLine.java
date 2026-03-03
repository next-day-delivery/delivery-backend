package com.nextdaydelivery.order_line.domain.entity;

import com.nextdaydelivery.global.baseEntity.BaseEntity;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.product.domain.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_order_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderLine extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_line_id", updatable = false, nullable = false)
    private UUID orderLineId; // 주문품목 PK (JPA 관례에 따른 식별자 추가)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 단방향: OrderLine에서 Order를 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 단방향: OrderLine에서 Product를 참조

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량 (BIGINT)

    @Column(name = "price", nullable = false)
    private Long price; // 가격 (BIGINT)
}
