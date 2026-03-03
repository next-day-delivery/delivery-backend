package com.nextdaydelivery.cart_item.domain.entity;

import com.nextdaydelivery._domainName_sample.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "p_cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cart_item_id", updatable = false, nullable = false)
    private UUID cartItemId; // 품목 고유 PK (JPA 관례상 추가)

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량 (BIGINT)

    @Column(name = "cart_id", nullable = false)
    private UUID cartId; // 장바구니 PK

    @Column(name = "product_id", nullable = false)
    private UUID productId; // 상품 PK
}
