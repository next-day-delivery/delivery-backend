package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse requestPayment(PaymentRequest request);

    PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest);
}
