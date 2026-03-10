package com.nextdaydelivery.payment.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.payment.application.service.PaymentConfirmFacade;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentConfirmFacade paymentApprovalFacade;


    @PostMapping("/confirm")
    public CommonResponse<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request,
                                                                 @AuthenticationPrincipal
                                                                 PrincipalDetails details) {
        PaymentConfirmResponse response = paymentApprovalFacade.confirm(request, details.getAuthUserDto().userId());
        return CommonResponse.onSuccess(response);
    }

}
