package com.nextdaydelivery.cart.domain.entity;

import com.nextdaydelivery.cart.domain.enums.CartStatus;
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
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cart_id", updatable = false, nullable = false)
    private UUID cartId; // 장바구니 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 가게 PK

    @Enumerated(EnumType.STRING) // DB에 문자열(ACTIVE 등)로 저장
    @Column(name = "status", nullable = false)
    private CartStatus status; // 상태 (ACTIVE, INACTIVE, COMPLETED)

    @Builder
    public static Cart createActive(User user, Store store, CartStatus status) {
        return Cart.builder()
            .user(user)
            .store(store)
            .status(status)
            .build();
    }

    public void markInactive() {
        this.status = CartStatus.INACTIVE;
    }

    public void markCompleted() {
        this.status = CartStatus.COMPLETED;
    }
}
