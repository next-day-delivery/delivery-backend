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
        if (cartItems.isEmpty()) {
            throw new BusinessException(CartErrorCode.CART_EMPTY);
        }
        Checkout activeCheckout = checkoutRepository.findActivePendingByCartId(request.cartId(),
                CheckoutStatus.PAYMENT_PENDING).orElse(null);
        String requestHash = generateHash(cartItems, request);

        if (activeCheckout != null) {
            validateCheckoutAccess(activeCheckout, userId);
            if (activeCheckout.isExpired()) {
                activeCheckout.markExpired();
            } else if (activeCheckout.isSameSnapshot(requestHash, request.amount())) {
                return CheckoutResponse.from(activeCheckout);
            } else {
                activeCheckout.markExpired();
            }
        }

        JsonNode snapshot = buildOrderSnapshot(cartItems, request, userId);
        Checkout newCheckout = Checkout.of(request, userId, requestHash, generateOrderNumber(userId),
                snapshot);
        try {
            checkoutRepository.saveAndFlush(newCheckout);
            return CheckoutResponse.from(newCheckout);
        } catch (DataIntegrityViolationException e) {
            return checkoutRepository.findActivePendingByCartId(request.cartId(), CheckoutStatus.PAYMENT_PENDING)
                    .map(CheckoutResponse::from)
                    .orElseThrow(() -> new BusinessException(CheckoutErrorCode.CHECKOUT_CONCURRENCY_ERROR));
        }
    }

    @Transactional
    @Override
    public Checkout getValidatedCheckout(PaymentConfirmRequest request, Long userId) {
        Checkout checkout = checkoutRepository.findById(request.checkoutId())
                .orElseThrow(() -> new BusinessException(CheckoutErrorCode.CHECKOUT_NOT_FOUND));
        checkout.validate(userId, request.orderNo(), request.amount());
        return checkout;
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
