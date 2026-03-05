package com.nextdaydelivery.payment.application.service;

import com.nextdaydelivery.payment.domain.entity.Payment;
import com.nextdaydelivery.payment.domain.repository.PaymentRepository;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentResponse;
import java.util.UUID;
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

    @Transactional
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 결제 아이디 입니다."));

        payment.validatePendingStatus();
        // 금액 검증

        //결제 시도 로직 (현재는 success로만 바꾸기)
        payment.processPayment();

        //카트로 주문 생성
        //장바구니 비우기
        return new PaymentConfirmResponse(UUID.randomUUID());
    }


}
