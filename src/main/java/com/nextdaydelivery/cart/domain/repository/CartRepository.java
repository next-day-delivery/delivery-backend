package com.nextdaydelivery.cart.domain.repository;

import com.nextdaydelivery.cart.domain.entity.Cart;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, UUID>, CartQueryRepository{
}
