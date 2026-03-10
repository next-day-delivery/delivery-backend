package com.nextdaydelivery.payment.domain.repository;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;

public interface PaymentRepository {
    boolean existsByOrderNoAndPaymentStatus(String orderNo, PaymentStatus status);

    boolean existsByOrderNo(String orderNo);

    Payment save(Payment payment);
}
