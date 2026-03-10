package com.nextdaydelivery.checkout.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.cart.application.service.CartService;
import com.nextdaydelivery.cart.domain.repository.CartRepository;
import com.nextdaydelivery.cart_item.domain.repository.CartItemRepository;
import com.nextdaydelivery.cart_item.domain.repository.CartItemSummary;
import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.checkout.domain.repository.CheckoutRepository;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import com.nextdaydelivery.checkout.presentation.dto.response.CheckoutResponse;
import com.nextdaydelivery.global.domain.error.CartErrorCode;
import com.nextdaydelivery.global.domain.error.CheckoutErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.application.dto.OrderSnapshot;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CheckoutRepository checkoutRepository;
    private final ObjectMapper objectMapper;
    private final CartService cartService;


    @Transactional
    public CheckoutResponse createOrUpdateCheckout(CheckoutRequest request, Long userId) {
        List<CartItemSummary> cartItems = cartItemRepository.findActiveCartItemsByUserId(userId);
        validateCartAndAmount(cartItems, request);

        String requestHash = generateHash(cartItems, request);

        return checkoutRepository.findActivePendingByCartId(request.cartId(), CheckoutStatus.PAYMENT_PENDING)
                .filter(active -> isUsable(active, userId, requestHash, request.amount()))
                .map(CheckoutResponse::from)
                .orElseGet(() -> createNewCheckout(request, userId, requestHash, cartItems));

    }


    @Transactional
    @Override
    public Checkout getValidatedCheckout(PaymentConfirmRequest request, Long userId) {
        Checkout checkout = checkoutRepository.findById(request.checkoutId())
                .orElseThrow(() -> new BusinessException(CheckoutErrorCode.CHECKOUT_NOT_FOUND));
        checkout.validate(userId, request.orderNo(), request.amount());
        return checkout;
    }

    private boolean isUsable(Checkout active, Long userId, String requestHash, Long amount) {
        validateCheckoutAccess(active, userId);
        if (active.isExpired() || !active.isSameSnapshot(requestHash, amount)) {
            active.markExpired();
            return false;
        }
        return true;
    }

    private CheckoutResponse createNewCheckout(CheckoutRequest request, Long userId, String requestHash,
                                               List<CartItemSummary> cartItems) {
        JsonNode snapshot = buildOrderSnapshot(cartItems, request, userId);
        Checkout newCheckout = Checkout.of(request, userId, requestHash, generateOrderNumber(userId), snapshot);

        try {
            return CheckoutResponse.from(checkoutRepository.saveAndFlush(newCheckout));
        } catch (DataIntegrityViolationException e) {
            return checkoutRepository.findActivePendingByCartId(request.cartId(), CheckoutStatus.PAYMENT_PENDING)
                    .map(CheckoutResponse::from)
                    .orElseThrow(() -> new BusinessException(CheckoutErrorCode.CHECKOUT_CONCURRENCY_ERROR));
        }
    }

    private void validateCartAndAmount(List<CartItemSummary> cartItems, CheckoutRequest request) {
        if (cartItems.isEmpty()) {
            throw new BusinessException(CartErrorCode.CART_EMPTY);
        }

        UUID actualCartId = cartItems.get(0).cartId();
        if (!actualCartId.equals(request.cartId())) {
            throw new BusinessException(CartErrorCode.CART_ID_MISMATCH);
        }

        long serverCalculatedTotal = cartItems.stream()
                .mapToLong(item -> item.price().longValue() * item.quantity())
                .sum();

        if (serverCalculatedTotal != request.amount()) {
            throw new BusinessException(CheckoutErrorCode.AMOUNT_MISMATCH);
        }
    }


    private String generateHash(List<CartItemSummary> items, CheckoutRequest request) {
        String itemsPart = items.stream()
                .sorted(Comparator.comparing(CartItemSummary::productId))
                .map(item -> item.productId() + ":" + item.quantity() + ":" + item.price())
                .collect(Collectors.joining("|"));
        String rawPayload = String.format("cart:%s|store:%s|amount:%d|items:%s",
                request.cartId(),
                request.storeId(),
                request.amount(),
                itemsPart);

        return DigestUtils.sha256Hex(rawPayload);
    }


    private void validateCheckoutAccess(Checkout checkout, Long userId) {
        if (!checkout.getUserId().equals(userId)) {
            throw new BusinessException(CheckoutErrorCode.CHECKOUT_ACCESS_DENIED);
        }
    }

    private JsonNode buildOrderSnapshot(List<CartItemSummary> items, CheckoutRequest request, Long userId) {
        OrderSnapshot snapshot = OrderSnapshot.from(items, request, userId);
        return objectMapper.valueToTree(snapshot);
    }

    private String generateOrderNumber(Long userId) {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return String.format("CHK-%s-%d-%s", datePrefix, userId, randomSuffix);
    }

    @Override
    @Transactional
    public void markPaid(Checkout checkout, String paymentKey) {
        checkout.completePaid(paymentKey);
    }

    @Override
    @Transactional
    public void expireCheckout(Checkout checkout) {
        checkout.markExpired();
    }


}
