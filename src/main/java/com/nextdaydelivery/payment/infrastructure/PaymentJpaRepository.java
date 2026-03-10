package com.nextdaydelivery.payment.infrastructure;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.orderNo = :orderNo AND p.paymentStatus = :status")
    boolean existsByOrderNoAndPaymentStatus(@Param("orderNo") String orderNo, @Param("status") PaymentStatus status);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.orderNo = :orderNo")
    boolean existsByOrderNo(@Param("orderNo") String orderNo);
}
