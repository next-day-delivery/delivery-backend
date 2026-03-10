package com.nextdaydelivery.cart.domain.repository;

import com.nextdaydelivery.cart.domain.entity.Cart;
import java.util.Optional;

public interface CartQueryRepository {

    Optional<Cart> findActiveCartByUserId(Long userId);
}
