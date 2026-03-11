package com.nextdaydelivery.payment.domain.entity;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.global.domain.entity.CreatedAuditEntity;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
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
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends CreatedAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId; // 결제 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 결제에서 주문을 참조

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // 결제 방법 (CARD 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태 (CANCEL, COMPLETE, FAILED)

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;


    @Builder
    public Payment(PaymentMethod method, PaymentStatus status, String paymentKey, Long amount, User user,
                   String orderNo) {
        this.paymentMethod = method;
        this.paymentStatus = status;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.user = user;
        this.orderNo = orderNo;
    }


    public static Payment of(Checkout checkout, String paymentKey, User user) {
        return Payment.builder()
                .orderNo(checkout.getOrderNo())
                .status(PaymentStatus.PENDING)
                .method(checkout.getPaymentMethod())
                .paymentKey(paymentKey)
                .amount(checkout.getAmount())
                .user(user)
                .build();

    }

    public void fail() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }

    public void complete(Order order) {
        this.order = order;
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void cancelFail() {
        this.paymentStatus = PaymentStatus.CANCELFAILED;
    }


}
