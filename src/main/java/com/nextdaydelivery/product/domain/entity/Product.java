package com.nextdaydelivery.product.domain.entity;

import com.nextdaydelivery.global.baseEntity.BaseEntity;
import com.nextdaydelivery.store.domain.entity.Store;
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
@Table(name = "p_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId; // 상품 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 상품이 속한 가게 참조

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName; // 상품명

    @Column(name = "product_detail", length = 255)
    private String productDetail; // 상품 설명

    @Column(name = "price", nullable = false)
    private Integer price; // 상품 가격 (INT)

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden; // 숨김 여부 (BOOLEAN)
}
