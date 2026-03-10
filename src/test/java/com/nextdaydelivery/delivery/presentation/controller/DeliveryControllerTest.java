package com.nextdaydelivery.delivery.presentation.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.delivery.application.service.DeliveryService;
import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import com.nextdaydelivery.delivery.presentation.dto.request.DeliveryStatusRequest;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.jwt.JwtValidator;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeliveryService deliveryService;

    @MockitoBean
    private JwtValidator jwtValidator;

    @Test
    @DisplayName("사장님용 배송상태 업데이트_성공")
    void updateDeliveryStatusByOwner_Success() throws Exception {
        UUID deliveryId = UUID.randomUUID();
        Long userId = 1L;
        DeliveryStatusRequest request = new DeliveryStatusRequest(DeliveryStatus.DELIVERY_ING);

        AuthUserDto authUserDto = new AuthUserDto(userId, UserRole.OWNER);
        PrincipalDetails principalDetails = new PrincipalDetails(authUserDto);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}", deliveryId)
                        .with(csrf()) // Security 설정에 따라 필요 시 추가
                        .with(user(principalDetails)) // @AuthenticationPrincipal 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS")) // isSuccess -> result로 변경
                .andExpect(jsonPath("$.code").value("200"));

        verify(deliveryService).updateDeliveryStatusByOwner(eq(deliveryId), eq(DeliveryStatus.DELIVERY_ING),
                eq(userId));
    }

    @Test
    @DisplayName("매니저용 배송상태 업데이트_성공")
    @WithMockUser(roles = "MANAGER")
    void updateDeliveryStatusByManager_Success() throws Exception {
        UUID deliveryId = UUID.randomUUID();
        DeliveryStatusRequest request = new DeliveryStatusRequest(DeliveryStatus.DELIVERY_COMPLETED);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/manager", deliveryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.result").value("SUCCESS")) // isSuccess -> result로 변경
                .andExpect(jsonPath("$.code").value("200"));

        verify(deliveryService).updateDeliveryStatusByManager(eq(deliveryId), eq(DeliveryStatus.DELIVERY_COMPLETED));
    }
}