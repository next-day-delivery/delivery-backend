package com.nextdaydelivery.payment.infrastructure;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import com.nextdaydelivery.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository jpaRepository;

    @Override
    public boolean existsByOrderNoAndPaymentStatus(String orderNo, PaymentStatus status) {
        return jpaRepository.existsByOrderNoAndPaymentStatus(orderNo, status);
    }

    @Override
    public boolean existsByOrderNo(String orderNo) {
        return jpaRepository.existsByOrderNo(orderNo);
    }

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }
}
