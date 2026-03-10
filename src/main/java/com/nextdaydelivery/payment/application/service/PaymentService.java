package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResult;
import com.nextdaydelivery.user.domain.entity.User;

public interface PaymentService {
    Payment createPayment(Checkout checkout, String paymentKey, User user);

    boolean isAlreadyPaid(String orderNo);

    PaymentConfirmResult requestPgApproval(PaymentConfirmRequest request);

    void markCompleted(Payment payment, Order order);


    void markFailed(Payment payment);

    void markCanceled(Payment payment);

    void markCancelFailed(Payment payment);

}
