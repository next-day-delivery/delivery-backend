package com.nextdaydelivery.checkout.infrastructure.repository;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.checkout.domain.repository.CheckoutRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CheckoutRepositoryImpl implements CheckoutRepository {
    private final CheckoutJpaRepository jpaRepository;

    @Override
    public Optional<Checkout> findActivePendingByCartId(UUID cartId, CheckoutStatus status) {
        return jpaRepository.findActivePendingByCartId(cartId, status);
    }

    @Override
    public void saveAndFlush(Checkout checkout) {
        jpaRepository.saveAndFlush(checkout);
    }

    @Override
    public Optional<Checkout> findById(UUID checkoutId) {
        return jpaRepository.findById(checkoutId);
    }
}
