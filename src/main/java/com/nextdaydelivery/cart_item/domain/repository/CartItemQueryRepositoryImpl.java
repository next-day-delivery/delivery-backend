package com.nextdaydelivery.cart_item.domain.repository;

import static com.nextdaydelivery.cart.domain.entity.QCart.cart;
import static com.nextdaydelivery.cart_item.domain.entity.QCartItem.cartItem;
import static com.nextdaydelivery.product.domain.entity.QProduct.product;

import com.nextdaydelivery.cart.domain.entity.Cart;
import com.nextdaydelivery.cart.domain.enums.CartStatus;
import com.nextdaydelivery.cart_item.domain.entity.CartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
//@ConditionalOnBean(JPAQueryFactory.class)
public class CartItemQueryRepositoryImpl implements CartItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId) {
        // cartId + productId로 장바구니 품목 1건 조회(있으면 반환)
        CartItem found = queryFactory
            .selectFrom(cartItem)
            .where(
                cartItem.cart.cartId.eq(cartId),
                cartItem.product.productId.eq(productId)
            )
            .fetchOne();

        return Optional.ofNullable(found);
    }

    @Override
    public List<CartItemSummary> findActiveCartItemsByUserId(Long userId) {
        // 1. 최신 ACTIVE cart 1건 확정
        Cart activeCart = queryFactory
                .selectFrom(cart)
                .where(cart.user.userId.eq(userId), cart.status.eq(CartStatus.ACTIVE))
                .orderBy(cart.cartId.desc())
                .limit(1)
                .fetchFirst();

        if (activeCart == null) return Collections.emptyList();

        // 2. 확정된 cartId로만 item 조회
        return queryFactory
                .select(new QCartItemSummary(
                        cart.cartId,
                        cart.store.storeId,
                        product.productId,
                        product.productName,
                        product.price,
                        cartItem.quantity
                ))
                .from(cartItem)
                .join(cartItem.cart, cart)
                .join(cartItem.product, product)
                .where(
                        cart.cartId.eq(activeCart.getCartId())  // ← userId+ACTIVE 대신 cartId
                )
                .fetch();
    }

    @Override
    public long deleteByCartIdAndProductId(UUID cartId, UUID productId) {
        // cartId + productId 조건에 맞는 장바구니 품목 삭제
        return queryFactory
            .delete(cartItem)
            .where(
                cartItem.cart.cartId.eq(cartId),
                cartItem.product.productId.eq(productId)
            )
            .execute();
    }

    @Override
    public long deleteByCartId(UUID cartId) {
        return queryFactory
            .delete(cartItem)
            .where(cartItem.cart.cartId.eq(cartId))
            .execute();
    }
}
