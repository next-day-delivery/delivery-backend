package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.payment.presentation.dto.request.PaymentRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse requestPayment(PaymentRequest createRequest);
}
