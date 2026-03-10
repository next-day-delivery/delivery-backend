package com.nextdaydelivery.order.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.dto.CommonResponse.Result;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.jwt.JwtAuthenticationFilter;
import com.nextdaydelivery.global.security.jwt.JwtValidator;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) //시큐리티 필터 체인 안거치게
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtValidator jwtValidator;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("자신의 주문 목록 조회 성공")
    void getMyOrders_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderListResponse responseDto = OrderListResponse.builder()
                .orderId(orderId)
                .storeName("맛집")
                .totalPrice(10000L)
                .orderStatus(OrderStatus.ORDER_ACCEPTED.toString())
                .createdAt(LocalDateTime.now())
                .build();

        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);

        given(orderService.getOrdersByCustomer(any(), eq(10))).willReturn(slice);

        mockMvc.perform(get("/api/orders/me")
                        .header("X-User-Id", 1L)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.content[0].storeName").value("맛집"))
                .andExpect(jsonPath("$.data.content[0].totalPrice").value(10000L))
                .andExpect(jsonPath("$.data.content[0].orderStatus").value(OrderStatus.ORDER_ACCEPTED.toString()))
                .andDo(print());
    }


    @Test
    @DisplayName("가게의 주문 목록 조회 성공")
    void getStoreOrders_Success() throws Exception {
        UUID storeId = UUID.randomUUID();
        OrderListResponse responseDto = OrderListResponse.builder()
                .storeId(storeId)
                .ownerId(2L)
                .build();
        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);
        given(orderService.getStoreOrders(eq(storeId), any(), eq(2L), eq(10))).willReturn(slice);
        mockMvc.perform(get("/api/orders/store/{storeId}", storeId)
                        .header("X-User-Id", 2L)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.data.content[0].ownerId").value(2L))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 목록 검색 성공")
    void searchOrders_Success() throws Exception {
        UUID storeId = UUID.randomUUID();
        OrderSearchRequest request = OrderSearchRequest.builder()
                .storeId(storeId)
                .build();
        OrderListResponse responseDto = OrderListResponse.builder()
                .storeId(storeId)
                .ownerId(2L)
                .build();
        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);
        given(orderService.getOrdersByManager(any(), eq(10))).willReturn(slice);
        mockMvc.perform(post("/api/orders/search")
                        .header("X-User-Id", 3L)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.data.content[0].ownerId").value(2L))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void getOrderDetails_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDetailResponse response = OrderDetailResponse.builder()
                .orderId(orderId)
                .customerId(1L)
                .build();
        given(orderService.getOrderDetail(eq(orderId), any())).willReturn(response);
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.customerId").value(1L))
                .andDo(print());

    }


    @Test
    @DisplayName("주문 상세 조회 실패 - 자신의 주문이 아님")
    void getOrderDetails_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        given(orderService.getOrderDetail(eq(orderId), any())).willThrow(
                new BusinessException(OrderErrorCode.NOT_YOUR_ORDER));
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-User-Id", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(OrderErrorCode.NOT_YOUR_ORDER.getMessage()))
                .andDo(print());
    }


    @Test
    @DisplayName("주문 상세 조회 실패 - 가게의 주문이 아님")
    void getStoreOrderDetail_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        given(orderService.getOrderDetail(eq(orderId), eq(1L))).willThrow(
                new BusinessException(OrderErrorCode.NOT_YOUR_STORE_ORDER));
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-User-Id", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(OrderErrorCode.NOT_YOUR_STORE_ORDER.getMessage()))
                .andDo(print());
    }


    @Test
    @DisplayName("주문 취소 성공 - 5분 이내")
    void cancelOrder_Success() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/cancel", UUID.randomUUID())
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).cancelOrderByCustomer(any(), any());

    }

    @Test
    @DisplayName("주문 취소 실패 - 5분 초과")
    void cancelOrder_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        doThrow(new BusinessException(OrderErrorCode.CANCEL_TIMEOUT))
                .when(orderService).cancelOrderByCustomer(eq(orderId), any());

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                        .header("X-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value("O004"))
                .andExpect(jsonPath("$.message").value("주문 후 5분이 경과하여 취소할 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("사장님용 주문 거절 성공")
    void rejectOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        mockMvc.perform(post("/api/orders/{orderId}/reject", orderId)
                        .header("X-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).rejectOrder(eq(orderId), any());

    }

    @Test
    @DisplayName("매니저용 주문 취소 성공")
    void cancelOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        mockMvc.perform(post("/api/orders/{orderId}/cancel/manager", orderId)
                        .header("X-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).cancelOrderByManager(eq(orderId));

    }


    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 수락")
    void acceptOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_ACCEPTED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).changeOrderStatusByOwner(any(), eq(orderId), eq(1L));

    }

    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 주문 조리 완료")
    void cookOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).changeOrderStatusByOwner(any(), eq(orderId), eq(1L));

    }

    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 주문 완료")
    void completeOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COMPLETED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());

        verify(orderService, times(1)).changeOrderStatusByOwner(any(), eq(orderId), eq(1L));

    }


    @Test
    @DisplayName("사장님용 주문 상태 변경 실패 - 변경할 수 없는 상태값")
    void cookOrderByOwner_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        doThrow(new BusinessException(OrderErrorCode.INVALID_STATUS_CHANGE))
                .when(orderService).changeOrderStatusByOwner(any(), eq(orderId), eq(1L));
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value("O005"))
                .andExpect(jsonPath("$.message").value("변경 불가능한 주문 상태입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("매니저용 주문 상태 변경 성공 - 수락")
    void acceptOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_ACCEPTED);
        mockMvc.perform(patch("/api/orders/{orderId}/status/manager", orderId)
                        .header("X-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());

        verify(orderService, times(1)).changeOrderStatusByManager(any(), eq(orderId));

    }

    @Test
    @DisplayName("매니저용 주문 상태 변경 성공 - 주문 조리 완료")
    void cookOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        mockMvc.perform(patch("/api/orders/{orderId}/status/manager", orderId)
                        .header("X-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());

        verify(orderService, times(1)).changeOrderStatusByManager(any(), eq(orderId));


    }

    @Test
    @DisplayName("매니저용 주문 상태 변경 성공 - 주문 완료")
    void completeOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COMPLETED);
        mockMvc.perform(patch("/api/orders/{orderId}/status/manager", orderId)
                        .header("X-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).changeOrderStatusByManager(any(), eq(orderId));

    }


    @Test
    @DisplayName("매니저용 주문 상태 변경 실패 - 변경할 수 없는 상태값")
    void cookOrderByManager_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        doThrow(new BusinessException(OrderErrorCode.INVALID_STATUS_CHANGE))
                .when(orderService).changeOrderStatusByManager(any(), eq(orderId));
        mockMvc.perform(patch("/api/orders/{orderId}/status/manager", orderId)
                        .header("X-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value("O005"))
                .andExpect(jsonPath("$.message").value("변경 불가능한 주문 상태입니다."))
                .andDo(print());

    }

}

