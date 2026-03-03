package com.nextdaydelivery.cart.domain.entity;

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
@Table(name = "p_cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cart_id", updatable = false, nullable = false)
    private UUID cartId; // 장바구니 PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK

    @Column(name = "store_id", nullable = false)
    private UUID storeId; // 가게 PK

    @Enumerated(EnumType.STRING) // DB에 문자열(ACTIVE 등)로 저장
    @Column(name = "status", nullable = false)
    private CartStatus status; // 상태 (ACTIVE, INACTIVE, COMPLETED)

    // 장바구니 상태를 위한 ENUM
    public enum CartStatus {
        ACTIVE, INACTIVE, COMPLETED
    }
}
