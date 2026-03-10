package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;

public interface PaymentConfirmFacade {
    PaymentConfirmResponse confirm(PaymentConfirmRequest request, Long userId);
}
