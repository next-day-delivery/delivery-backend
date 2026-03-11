package com.nextdaydelivery.checkout.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import com.nextdaydelivery.global.domain.entity.BaseAuditEntity;
import com.nextdaydelivery.global.domain.error.CheckoutErrorCode;
import com.nextdaydelivery.global.domain.error.PaymentErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "p_checkout")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Checkout extends BaseAuditEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "checkout_id", nullable = false, updatable = false, length = 64)
    private UUID checkoutId;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "cart_hash", nullable = false, length = 128)
    private String cartHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_snapshot", nullable = false, columnDefinition = "jsonb")
    private JsonNode orderSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CheckoutStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "pg_payment_key", length = 100)
    private String pgPaymentKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;


    @Builder
    private Checkout(
            UUID cartId,
            Long userId,
            String orderNo,
            UUID storeId,
            Long amount,
            String cartHash,
            JsonNode orderSnapshot,
            PaymentMethod paymentMethod,
            LocalDateTime expiresAt
    ) {
        this.cartId = cartId;
        this.userId = userId;
        this.storeId = storeId;
        this.amount = amount;
        this.cartHash = cartHash;
        this.orderSnapshot = orderSnapshot;
        this.status = CheckoutStatus.PAYMENT_PENDING;
        this.expiresAt = expiresAt;
        this.orderNo = orderNo;
        this.paymentMethod = paymentMethod;
    }


    public boolean isExpired() {
        return this.expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isPaid() {
        return this.status == CheckoutStatus.PAID;
    }

    public boolean isPending() {
        return this.status == CheckoutStatus.PAYMENT_PENDING;
    }

    public void markExpired() {
        this.status = CheckoutStatus.EXPIRED;
    }


    public boolean isSameSnapshot(String cartHash, Long amount) {
        return this.cartHash.equals(cartHash) && this.amount.equals(amount);
    }

    public static Checkout of(CheckoutRequest request, Long userId, String cartHash, String orderNo,
                              JsonNode jsonNode) {

        return Checkout.builder()
                .amount(request.amount())
                .orderNo(orderNo)
                .cartHash(cartHash)
                .cartId(request.cartId())
                .userId(userId)
                .storeId(request.storeId())
                .orderSnapshot(jsonNode)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .paymentMethod(request.paymentMethod())
                .build();


    }

    public void validate(Long userId, String orderNo, Long amount) {
        if (!this.getUserId().equals(userId)) {
            throw new BusinessException(CheckoutErrorCode.CHECKOUT_ACCESS_DENIED);
        }
        if (!this.getOrderNo().equals(orderNo)) {
            throw new BusinessException(CheckoutErrorCode.INVALID_ORDER_NUMBER);
        }
        if (!this.getAmount().equals(amount)) {
            throw new BusinessException(CheckoutErrorCode.INVALID_CHECKOUT_AMOUNT);
        }
        if (this.isExpired()) {
            markExpired();
            throw new BusinessException(CheckoutErrorCode.CHECKOUT_EXPIRED);
        }
        if (isPaid()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_PAID_ALREADY);
        }
        if (!isPending()) {
            throw new BusinessException(CheckoutErrorCode.INVALID_CHECKOUT_STATUS);
        }
    }

    public void completePaid(String paymentKey) {
        this.pgPaymentKey = paymentKey;
        this.status = CheckoutStatus.PAID;
    }


}
