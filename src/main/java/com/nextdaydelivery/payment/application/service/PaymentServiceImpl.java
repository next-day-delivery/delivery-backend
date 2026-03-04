package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.repository.PaymentRepository;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;


    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
        //장바구니 검증 및 총 가격 가져오기
        Payment payment = Payment.from(request.paymentMethod());
        Payment savedPayment = paymentRepository.save(payment);
        return new PaymentResponse(savedPayment.getPaymentId(), null);
    }


}
