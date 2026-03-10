package com.nextdaydelivery.checkout.presentation.controller;

import com.nextdaydelivery.checkout.application.service.CheckoutService;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import com.nextdaydelivery.checkout.presentation.dto.response.CheckoutResponse;
import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkouts")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @RequireCustomerRole
    @PostMapping("/request")
    public CommonResponse<CheckoutResponse> requestCheckout(@RequestBody @Valid CheckoutRequest request,
                                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        CheckoutResponse response = checkoutService.createOrUpdateCheckout(request, principalDetails.getAuthUserDto()
                .userId());
        return CommonResponse.onSuccess(HttpStatus.CREATED, response);
    }
}
