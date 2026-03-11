package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.global.domain.error.PaymentErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import com.nextdaydelivery.payment.domain.repository.PaymentRepository;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResult;
import com.nextdaydelivery.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public Payment createPayment(Checkout checkout, String paymentKey, User user) {
        if (checkout.isPaid()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_PAID_ALREADY);
        }

        if (paymentRepository.existsByOrderNo(checkout.getOrderNo())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        Payment payment = Payment.of(checkout, paymentKey, user);
        try {
            return paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_CONCURRENCY_ERROR);
        }
    }

    @Override
    public boolean isAlreadyPaid(String orderNo) {
        return paymentRepository.existsByOrderNoAndPaymentStatus(orderNo, PaymentStatus.COMPLETED);
    }


    @Override
    public PaymentConfirmResult requestPgApproval(PaymentConfirmRequest request) {
        //외부 pg
        PaymentConfirmResult result = new PaymentConfirmResult(
                request.paymentKey(),
                request.orderNo(),
                request.amount()
        );
        validatePgResult(result, request);

        return result;
    }


    private void validatePgResult(PaymentConfirmResult result, PaymentConfirmRequest request) {
        if (!result.paymentKey().equals(request.paymentKey())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_INVALID_PAYMENT_KEY);
        }
        if (!result.amount().equals(request.amount())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_INVALID_CHECKOUT_AMOUNT);
        }
        if (!result.orderNo().equals(request.orderNo())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_INVALID_ORDER_NUMBER);
        }
    }

    @Override
    @Transactional
    public void markCompleted(Payment payment, Order order) {
        payment.complete(order);
    }

    @Transactional
    @Override
    public void markFailed(Payment payment) {
        payment.fail();
    }

    @Transactional
    @Override
    public void markCanceled(Payment payment) {
        payment.cancel();
    }

    @Transactional
    @Override
    public void markCancelFailed(Payment payment) {
        payment.cancelFail();
    }

    @Transactional
    @Override
    public void cancelPayment(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        markCanceled(payment);
    }


}
