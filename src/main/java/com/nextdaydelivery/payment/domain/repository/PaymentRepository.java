package com.nextdaydelivery.payment.domain.repository;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    boolean existsByOrderNoAndPaymentStatus(String orderNo, PaymentStatus status);

    boolean existsByOrderNo(String orderNo);

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(UUID orderId);
}
