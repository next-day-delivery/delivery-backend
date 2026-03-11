package com.nextdaydelivery.checkout.infrastructure.repository;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckoutJpaRepository extends JpaRepository<Checkout, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c
            from Checkout c
            where c.cartId = :cartId
              and c.status = :status
            """)
    Optional<Checkout> findActivePendingByCartId(@Param("cartId") UUID cartId,
                                                 @Param("status") CheckoutStatus status);
}
