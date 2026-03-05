package com.nextdaydelivery.cart_item.domain.repository;

import static com.nextdaydelivery.cart.domain.entity.QCart.cart;
import static com.nextdaydelivery.cart_item.domain.entity.QCartItem.cartItem;
import static com.nextdaydelivery.product.domain.entity.QProduct.product;

import com.nextdaydelivery.cart.domain.enums.CartStatus;
import com.nextdaydelivery.cart_item.domain.entity.CartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
        // 사용자 ACTIVE cart의 품목 목록을 상품 정보와 함께 조회
        return queryFactory
            .select(
                new QCartItemSummary(
                    cart.cartId,
                    cart.store.storeId,
                    product.productId,
                    product.productName,
                    product.price,
                    cartItem.quantity
                )
            )
            .from(cartItem)
            .join(cartItem.cart, cart)
            .join(cartItem.product, product)
            .where(
                cart.user.userId.eq(userId),
                cart.status.eq(CartStatus.ACTIVE)
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
}
