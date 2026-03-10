package com.nextdaydelivery.payment.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.jwt.JwtValidator;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.payment.application.service.PaymentConfirmFacade;
import com.nextdaydelivery.payment.domain.enums.PaymentStatus;
import com.nextdaydelivery.payment.presentation.dto.request.PaymentConfirmRequest;
import com.nextdaydelivery.payment.presentation.dto.response.PaymentConfirmResponse;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtValidator jwtValidator;

    @MockitoBean
    private PaymentConfirmFacade paymentConfirmFacade;

    private AuthUserDto mockUser;
    private PrincipalDetails principal;

    @BeforeEach
    void setUp() {
        // 인증된 유저 정보 세팅 (OWNER 권한)
        mockUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        principal = new PrincipalDetails(mockUser);
    }

    @Test
    @DisplayName("결제 승인 요청 성공 - 200 OK")
    @WithMockUser(roles = "CUSTOMER")
        // 권한 체크 통과용
    void confirmPayment_Success() throws Exception {
        // given
        UUID checkoutId = UUID.randomUUID();
        String orderNo = "CHK-20260310-USER1-12345";

        PaymentConfirmRequest request = new PaymentConfirmRequest(
                checkoutId,
                orderNo,
                "toss_payment_key_abc123",
                55000L
        );

        PaymentConfirmResponse response = PaymentConfirmResponse.success(
                UUID.randomUUID(), // orderId
                orderNo,
                CheckoutStatus.PAID,            // checkoutStatus
                PaymentStatus.COMPLETED        // paymentStatus
        );

        // 파사드 로직 모킹
        given(paymentConfirmFacade.confirm(any(PaymentConfirmRequest.class), any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/payment/confirm")
                        .with(user(principal)).with(csrf()) // CSRF 필터 대응
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(orderNo))
                .andExpect(jsonPath("$.data.paymentStatus").value("COMPLETED"));
    }
}