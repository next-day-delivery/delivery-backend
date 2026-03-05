package com.nextdaydelivery.cart_item.domain.repository;

import com.nextdaydelivery.cart_item.domain.entity.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemQueryRepository {

    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    List<CartItemSummary> findActiveCartItemsByUserId(Long userId);

    long deleteByCartIdAndProductId(UUID cartId, UUID productId);
}
