package com.nextdaydelivery.checkout.domain.repository;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutRepository {


    Optional<Checkout> findActivePendingByCartId(UUID cartId,
                                                 CheckoutStatus status);

    void saveAndFlush(Checkout checkout);

    Optional<Checkout> findById(UUID checkoutId);
}

