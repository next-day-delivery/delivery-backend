package com.nextdaydelivery.cart.application.service;

import com.nextdaydelivery.cart.domain.entity.Cart;
import com.nextdaydelivery.global.domain.error.CartErrorCode;
import com.nextdaydelivery.cart.domain.enums.CartStatus;
import com.nextdaydelivery.cart.domain.repository.CartRepository;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPostCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResGetCartItemsDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPostCartItemDto;
import com.nextdaydelivery.cart_item.domain.entity.CartItem;
import com.nextdaydelivery.cart_item.domain.repository.CartItemRepository;
import com.nextdaydelivery.cart_item.domain.repository.CartItemSummary;
import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.repository.ProductRepository;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.infrastructure.UserJpaRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public ResPostCartItemDto addCartItem(Long userId, ReqPostCartItemDto request) {
        User user = getUser(userId);
        Product product = getProduct(request.productId());
        Store targetStore = product.getStore();

        // 이미 targetStore에 생성된 장바구니가 있으면 반환 , 다른가게의 장바구니를 담았다면 장바구니를 새로 생성(초기상태 ACTIVE)
        Cart activeCart = getOrCreateActiveCart(user, targetStore);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(activeCart.getCartId(), request.productId())
            .map(existingItem -> { // cartItem 이 존재하면 수량 증가
                existingItem.increaseQuantity(request.quantity());
                return existingItem;
            })
            .orElseGet(() -> CartItem.create(activeCart, product, request.quantity())); // cartItem이 존재하지 않으면 CartItem 생성

        CartItem saved = cartItemRepository.save(cartItem);

        return new ResPostCartItemDto(
            activeCart.getCartId(),
            activeCart.getStore().getStoreId(),
            saved.getProduct().getProductId(),
            saved.getQuantity(),
            activeCart.getStatus()
        );
    }

    // 현재 사용자가 사용중인 장바구니의 품목들을 반환
    public ResGetCartItemsDto getActiveCartItems(Long userId) {
        validateUser(userId);

        // QueryProjection 방식 ( for N+1 문제 해결 )
        List<CartItemSummary> items = cartItemRepository.findActiveCartItemsByUserId(userId);
        if (items.isEmpty()) { // cartItem이 없다면
            return cartRepository.findActiveCartByUserId(userId)
                .map(activeCart -> new ResGetCartItemsDto(   // 장바구니가 ACTIVE 이지만 CartItem이 존재하지 않을 경우
                    activeCart.getCartId(),
                    activeCart.getStore().getStoreId(),
                    activeCart.getStatus(),
                    Collections.emptyList()
                ))
                .orElseGet(() -> new ResGetCartItemsDto(null, null, CartStatus.ACTIVE, Collections.emptyList())); // 장바
        }

        // cartItem 이 존재한다면 , DTO 변환
        List<ResGetCartItemsDto.CartItemDetail> details = items.stream()
            .map(item -> new ResGetCartItemsDto.CartItemDetail(
                item.productId(),
                item.productName(),
                item.price(),
                item.quantity()
            ))
            .toList();

        CartItemSummary first = items.getFirst();
        return new ResGetCartItemsDto(
            first.cartId(),
            first.storeId(),
            CartStatus.ACTIVE,
            details
        );
    }

    @Transactional
    public ResPatchCartItemDto updateCartItem(Long userId, UUID productId, ReqPatchCartItemDto request) {
        validateUser(userId);

        Cart activeCart = getActiveCart(userId);
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(activeCart.getCartId(), productId)
            .orElseThrow(() -> new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.changeQuantity(request.quantity());
        CartItem saved = cartItemRepository.save(cartItem);

        return new ResPatchCartItemDto(
            activeCart.getCartId(),
            activeCart.getStore().getStoreId(),
            saved.getProduct().getProductId(),
            saved.getQuantity(),
            activeCart.getStatus()
        );
    }

    @Transactional
    public void deleteCartItem(Long userId, UUID productId) {
        validateUser(userId);

        Cart activeCart = getActiveCart(userId);
        long deletedCount = cartItemRepository.deleteByCartIdAndProductId(activeCart.getCartId(), productId);
        if (deletedCount == 0) {
            throw new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteActiveCart(Long userId) {
        validateUser(userId);

        Cart activeCart = getActiveCart(userId);
        cartItemRepository.deleteByCartId(activeCart.getCartId());
        cartRepository.delete(activeCart);
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(CartErrorCode.PRODUCT_NOT_FOUND));
    }

    private User getUser(Long userId) {
        return userJpaRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CartErrorCode.USER_NOT_FOUND));
    }

    private Cart getOrCreateActiveCart(User user, Store targetStore) {
        return cartRepository.findActiveCartByUserId(user.getUserId())
            .map(activeCart -> {
                if (!activeCart.getStore().getStoreId().equals(targetStore.getStoreId())) {
                    activeCart.markInactive();
                    return cartRepository.save(Cart.createActive(user, targetStore));
                }
                return activeCart;
            })
            .orElseGet(() -> cartRepository.save(Cart.createActive(user, targetStore)));
    }

    private Cart getActiveCart(Long userId) {
        return cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new BusinessException(CartErrorCode.ACTIVE_CART_NOT_FOUND));
    }

    private void validateUser(Long userId) {  // Todo : 향후 Jwt방식으로 변경 필요
        if (!userJpaRepository.existsById(userId)) {
            throw new BusinessException(CartErrorCode.USER_NOT_FOUND);
        }
    }
}
