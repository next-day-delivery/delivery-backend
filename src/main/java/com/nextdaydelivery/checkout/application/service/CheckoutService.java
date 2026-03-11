package com.nextdaydelivery.checkout.application.service;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import com.nextdaydelivery.checkout.presentation.dto.response.CheckoutResponse;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;

public interface CheckoutService {
    CheckoutResponse createOrUpdateCheckout(CheckoutRequest request, Long userId);

    Checkout getValidatedCheckout(PaymentConfirmRequest request, Long userId);

    void markPaid(Checkout checkout, String paymentKey);

    void expireCheckout(Checkout checkout);
}
