package com.nextdaydelivery.cart.domain.repository;

import static com.nextdaydelivery.cart.domain.entity.QCart.cart;

import com.nextdaydelivery.cart.domain.entity.Cart;
import com.nextdaydelivery.cart.domain.enums.CartStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartQueryRepositoryImpl implements CartQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Cart> findActiveCartByUserId(Long userId) {
        // userId 기준으로 가장 최근 ACTIVE cart 1건 조회
        Cart activeCart = queryFactory
            .selectFrom(cart)
            .where(
                cart.user.userId.eq(userId),
                cart.status.eq(CartStatus.ACTIVE)
            )
            .orderBy(cart.cartId.desc())
            .fetchFirst();

        return Optional.ofNullable(activeCart);
    }
}
