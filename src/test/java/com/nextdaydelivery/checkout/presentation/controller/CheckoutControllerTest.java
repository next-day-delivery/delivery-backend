package com.nextdaydelivery.checkout.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.checkout.application.service.CheckoutService;
import com.nextdaydelivery.checkout.domain.enums.CheckoutStatus;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutItemRequest;
import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import com.nextdaydelivery.checkout.presentation.dto.response.CheckoutResponse;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.payment.application.service.PaymentConfirmFacade;
import com.nextdaydelivery.payment.domain.enums.PaymentMethod;
import com.nextdaydelivery.payment.presentation.controller.PaymentController;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;
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

@WebMvcTest(controllers = {PaymentController.class, CheckoutController.class})
class CheckoutControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentConfirmFacade paymentApprovalFacade;

    @MockitoBean
    private CheckoutService checkoutService;

    private AuthUserDto mockUser;
    private PrincipalDetails principal;

    @BeforeEach
    void setUp() {
        // 인증된 유저 정보 세팅 (OWNER 권한)
        mockUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        principal = new PrincipalDetails(mockUser);
    }

    @Test
    @DisplayName("체크아웃 요청 성공 테스트")
    @WithMockUser
        // 인증 필터 통과용
    void requestCheckoutTest() throws Exception {
        // given
        List<CheckoutItemRequest> items = List.of(
                new CheckoutItemRequest(UUID.randomUUID(), "테스트 상품", 1, 50000L)
        );
        CheckoutRequest request = new CheckoutRequest(UUID.randomUUID(), UUID.randomUUID(), 50000L, "강남구", items,
                PaymentMethod.CARD);
        CheckoutResponse response = new CheckoutResponse(UUID.randomUUID(), "CHK-20260310-1", UUID.randomUUID(), 50000L,
                PaymentMethod.CARD, CheckoutStatus.PAYMENT_PENDING, LocalDateTime.now());

        given(checkoutService.createOrUpdateCheckout(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/checkouts/request")
                        .with(user(principal)).with(csrf()) // Security 설정 시 필요
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNo").value("CHK-20260310-1"));
    }
}