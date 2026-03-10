package com.nextdaydelivery.cart_item.domain.repository;

import com.nextdaydelivery.cart_item.domain.entity.CartItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID>, CartItemQueryRepository {
}
