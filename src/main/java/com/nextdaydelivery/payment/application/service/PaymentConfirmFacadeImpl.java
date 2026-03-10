package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.checkout.application.service.CheckoutService;
import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.global.domain.error.PaymentErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResult;
import com.nextdaydelivery.user.application.UserService;
import com.nextdaydelivery.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConfirmFacadeImpl implements PaymentConfirmFacade {
    private final PaymentService paymentService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final UserService userService;

    @Override
    public PaymentConfirmResponse confirm(PaymentConfirmRequest request, Long userId) {
        User user = userService.getById(userId);
        Checkout checkout = checkoutService.getValidatedCheckout(request, userId);
        if (paymentService.isAlreadyPaid(checkout.getOrderNo())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_PAID_ALREADY);
        }
        Payment payment = paymentService.createPayment(checkout, request.paymentKey(), user);

        PaymentConfirmResult result;
        try {
            result = paymentService.requestPgApproval(request);
        } catch (RuntimeException e) {
            failAndExpire(checkout, payment);
            throw e;
        }

        try {
            Order order = finalizeSuccess(checkout, payment, result, user);
            return PaymentConfirmResponse.success(
                    order.getOrderId(),
                    checkout.getOrderNo(),
                    checkout.getStatus(),
                    payment.getPaymentStatus()
            );
        } catch (RuntimeException e) {
            compensateAndExpire(checkout, payment);
            throw e;
        }
    }


    @Transactional
    protected Order finalizeSuccess(
            Checkout checkout,
            Payment payment,
            PaymentConfirmResult result,
            User user
    ) {
        Order order = orderService.createFromCheckout(checkout, user);

        paymentService.markCompleted(payment, order);
        checkoutService.markPaid(checkout, result.paymentKey());

        // 장바구니 비우기

        return order;
    }

    @Transactional
    protected void failAndExpire(
            Checkout checkout,
            Payment payment
    ) {
        paymentService.markFailed(payment);
        checkoutService.expireCheckout(checkout);
    }

    protected void compensateAndExpire(
            Checkout checkout,
            Payment payment
    ) {
        try {
            paymentService.markCanceled(payment);
        } catch (RuntimeException e) {
            paymentService.markCancelFailed(payment);
            throw e;
        } finally {
            checkoutService.expireCheckout(checkout);
        }
    }

}
