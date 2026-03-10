package com.nextdaydelivery.order.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.dto.CommonResponse.Result;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.order.application.fascade.OrderCancelFacade;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.domain.repository.dto.OrderLineInfo;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderLineSummary;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(controllers = {OrderController.class})
@AutoConfigureRestDocs
public class OrderControllerTest extends ControllerTestSupport {
    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderCancelFacade orderCancelFacade;


    private AuthUserDto customerDto;
    private PrincipalDetails customerPrincipal;
    private AuthUserDto ownerDto;
    private PrincipalDetails ownerPrincipal;
    private AuthUserDto managerDto;
    private PrincipalDetails managerPrincipal;

    @BeforeEach
    void setUp() {
        customerDto = new AuthUserDto(1L, UserRole.CUSTOMER);
        customerPrincipal = new PrincipalDetails(customerDto);

        ownerDto = new AuthUserDto(2L, UserRole.OWNER);
        ownerPrincipal = new PrincipalDetails(ownerDto);

        managerDto = new AuthUserDto(3L, UserRole.MANAGER);
        managerPrincipal = new PrincipalDetails(managerDto);

    }

    @Test
    @DisplayName("자신의 주문 목록 조회 성공")
    void getMyOrders_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderLineSummary line = new OrderLineSummary(UUID.randomUUID(), UUID.randomUUID(), "치킨", 1L);
        OrderListResponse responseDto = OrderListResponse.builder()
                .orderId(orderId)
                .storeName("맛집")
                .totalPrice(10000L)
                .orderStatus(OrderStatus.ORDER_ACCEPTED.toString())
                .createdAt(LocalDateTime.now())
                .orderLines(List.of(line))
                .build();

        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);

        given(orderService.getOrdersByCustomer(any(), eq(10))).willReturn(slice);

        mockMvc.perform(get("/api/orders/me")
                        .with(user(customerPrincipal))
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.content[0].storeName").value("맛집"))
                .andExpect(jsonPath("$.data.content[0].totalPrice").value(10000L))
                .andExpect(jsonPath("$.data.content[0].orderStatus").value(OrderStatus.ORDER_ACCEPTED.toString()))
                .andDo(document("order-my-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursor").optional().description("커서 ID (order UUID)"),
                                parameterWithName("size").optional().description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("result").description("요청 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"),

                                fieldWithPath("data.content[].orderId").description("주문 식별 ID (UUID)"),
                                fieldWithPath("data.content[].customerId").description("주문 고객 ID"),
                                fieldWithPath("data.content[].storeId").description("가게 식별 ID (UUID)"),
                                fieldWithPath("data.content[].storeName").description("가게 이름"),
                                fieldWithPath("data.content[].totalPrice").description("총 주문 금액"),
                                fieldWithPath("data.content[].orderStatus").description("주문 상태"),
                                fieldWithPath("data.content[].createdAt").description("주문 일시 (yyyy-MM-dd HH:mm:ss)"),

                                fieldWithPath("data.content[].orderLines[].orderLineId").description("주문 상세 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productId").description("상품 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productName").description("상품 이름"),
                                fieldWithPath("data.content[].orderLines[].quantity").description("주문 수량"),

                                fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.offset").description("전체 오프셋"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("비페이징 여부"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부"),

                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("페이지 번호"),
                                fieldWithPath("data.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                                fieldWithPath("data.empty").description("페이지가 비어있는지 여부")
                        )
                ));
        ;
    }


    @Test
    @DisplayName("가게의 주문 목록 조회 성공")
    void getStoreOrders_Success() throws Exception {
        UUID storeId = UUID.randomUUID();
        OrderLineSummary line = new OrderLineSummary(UUID.randomUUID(), UUID.randomUUID(), "치킨", 1L);
        OrderListResponse responseDto = OrderListResponse.builder()
                .orderId(UUID.randomUUID())
                .storeId(storeId)
                .ownerId(2L)
                .storeName("맛집")
                .totalPrice(10000L)
                .orderStatus(OrderStatus.ORDER_ACCEPTED.toString())
                .createdAt(LocalDateTime.now())
                .orderLines(List.of(line))
                .build();
        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);
        given(orderService.getStoreOrders(any(), any(), any(), anyInt())).willReturn(slice);
        mockMvc.perform(get("/api/orders/store/{storeId}", storeId)
                        .with(user(ownerPrincipal))
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.data.content[0].ownerId").value(2L))
                .andDo(document("order-store-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("storeId").description("가게 식별 ID (UUID)")
                        ),
                        queryParameters(
                                parameterWithName("cursor").optional().description("커서 ID (order UUID)"),
                                parameterWithName("size").optional().description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("result").description("요청 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"),

                                fieldWithPath("data.content[].orderId").description("주문 식별 ID (UUID)"),
                                fieldWithPath("data.content[].customerId").description("주문 고객 ID"),
                                fieldWithPath("data.content[].ownerId").description("가게 사장 ID"),
                                fieldWithPath("data.content[].storeId").description("가게 식별 ID (UUID)"),
                                fieldWithPath("data.content[].storeName").description("가게 이름"),
                                fieldWithPath("data.content[].totalPrice").description("총 주문 금액"),
                                fieldWithPath("data.content[].orderStatus").description("주문 상태"),
                                fieldWithPath("data.content[].createdAt").description("주문 일시 (yyyy-MM-dd HH:mm:ss)"),

                                fieldWithPath("data.content[].orderLines[].orderLineId").description("주문 상세 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productId").description("상품 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productName").description("상품 이름"),
                                fieldWithPath("data.content[].orderLines[].quantity").description("주문 수량"),

                                fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.offset").description("전체 오프셋"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("비페이징 여부"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부"),

                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("페이지 번호"),
                                fieldWithPath("data.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                                fieldWithPath("data.empty").description("페이지가 비어있는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("주문 목록 검색 성공")
    void searchOrders_Success() throws Exception {
        UUID storeId = UUID.randomUUID();
        OrderSearchRequest request = OrderSearchRequest.builder()
                .storeId(storeId)
                .build();
        OrderLineSummary line = new OrderLineSummary(UUID.randomUUID(), UUID.randomUUID(), "치킨", 1L);
        OrderListResponse responseDto = OrderListResponse.builder()
                .orderId(UUID.randomUUID())
                .storeId(storeId)
                .ownerId(2L)
                .storeName("맛집")
                .totalPrice(10000L)
                .orderStatus(OrderStatus.ORDER_ACCEPTED.toString())
                .createdAt(LocalDateTime.now())
                .orderLines(List.of(line))
                .build();
        Slice<OrderListResponse> slice = new SliceImpl<>(List.of(responseDto), PageRequest.of(0, 10), true);
        given(orderService.getOrdersByManager(any(), eq(10))).willReturn(slice);
        mockMvc.perform(post("/api/orders/search")
                        .with(user(managerPrincipal)).with(csrf())
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.data.content[0].ownerId").value(2L))
                .andDo(document("order-search-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("cursor").optional().description("커서 ID (order UUID)"),
                                parameterWithName("size").optional().description("페이지 크기"),
                                parameterWithName("_csrf").ignored()
                        ),
                        responseFields(
                                fieldWithPath("result").description("요청 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"),

                                fieldWithPath("data.content[].orderId").description("주문 식별 ID (UUID)"),
                                fieldWithPath("data.content[].customerId").description("주문 고객 ID"),
                                fieldWithPath("data.content[].ownerId").description("가게 사장 ID"),
                                fieldWithPath("data.content[].storeId").description("가게 식별 ID (UUID)"),
                                fieldWithPath("data.content[].storeName").description("가게 이름"),
                                fieldWithPath("data.content[].totalPrice").description("총 주문 금액"),
                                fieldWithPath("data.content[].orderStatus").description("주문 상태"),
                                fieldWithPath("data.content[].createdAt").description("주문 일시 (yyyy-MM-dd HH:mm:ss)"),

                                fieldWithPath("data.content[].orderLines[].orderLineId").description("주문 상세 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productId").description("상품 식별 ID"),
                                fieldWithPath("data.content[].orderLines[].productName").description("상품 이름"),
                                fieldWithPath("data.content[].orderLines[].quantity").description("주문 수량"),

                                fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.offset").description("전체 오프셋"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("비페이징 여부"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부"),

                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("페이지 번호"),
                                fieldWithPath("data.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                                fieldWithPath("data.empty").description("페이지가 비어있는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void getOrderDetails_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderLineInfo line = new OrderLineInfo(UUID.randomUUID(), UUID.randomUUID(), "치킨", 20000L, 1L);

        OrderDetailResponse response = OrderDetailResponse.builder()
                .orderId(orderId)
                .customerId(1L)
                .orderLines(List.of(line))
                .build();
        given(orderService.getOrderDetail(eq(orderId), eq(customerDto.userId()), eq(customerDto.role()))).willReturn(
                response);
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .with(user(customerPrincipal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.customerId").value(1L))
                .andDo(document("order-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        queryParameters(
                                parameterWithName("_csrf").ignored()
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"),

                                fieldWithPath("data.orderId").description("주문 식별 ID (UUID)"),
                                fieldWithPath("data.customerId").description("주문한 고객 ID"),
                                fieldWithPath("data.storeId").description("가게 식별 ID (UUID)"),
                                fieldWithPath("data.ownerId")
                                        .type(JsonFieldType.NUMBER) // [핵심] 타입을 직접 알려줌
                                        .description("사장님 ID (본인 주문 조회 시 NULL)")
                                        .optional(),
                                fieldWithPath("data.storeName").description("가게 이름"),
                                fieldWithPath("data.totalPrice").description("총 주문 금액"),
                                fieldWithPath("data.orderStatus").description("주문 상태"),
                                fieldWithPath("data.orderAddress").description("배송 주소"),
                                fieldWithPath("data.createdAt").description("주문 일시"),

                                fieldWithPath("data.orderLines").description("주문 상세 품목 리스트"),
                                fieldWithPath("data.orderLines[].orderLineId").description("주문 상세 식별 ID"),
                                fieldWithPath("data.orderLines[].productId").description("상품 식별 ID"),
                                fieldWithPath("data.orderLines[].productName").description("상품 이름"),
                                fieldWithPath("data.orderLines[].quantity").description("주문 수량"),
                                fieldWithPath("data.orderLines[].price").description("상품 단가")
                        )
                ));

    }


    @Test
    @DisplayName("주문 상세 조회 실패 - 자신의 주문이 아님")
    void getOrderDetails_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        given(orderService.getOrderDetail(eq(orderId), eq(customerDto.userId()), eq(customerDto.role()))).willThrow(
                new BusinessException(OrderErrorCode.NOT_YOUR_ORDER));
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .with(user(customerPrincipal)).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(OrderErrorCode.NOT_YOUR_ORDER.getMessage()))
                .andDo(print());
    }


    @Test
    @DisplayName("주문 상세 조회 실패 - 가게의 주문이 아님")
    void getStoreOrderDetail_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        given(orderService.getOrderDetail(any(), any(), any())).willThrow(
                new BusinessException(OrderErrorCode.NOT_YOUR_STORE_ORDER));
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .with(user(ownerPrincipal)).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(OrderErrorCode.NOT_YOUR_STORE_ORDER.getMessage()))
                .andDo(print());
    }


    @Test
    @DisplayName("주문 취소 성공 - 5분 이내")
    void cancelOrder_Success() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/cancel", UUID.randomUUID())
                        .with(user(customerPrincipal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(document("order-cancel-by-customer",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"))
                ));
        verify(orderCancelFacade, times(1)).cancelOrderByCustomer(any(), any());

    }

    @Test
    @DisplayName("주문 취소 실패 - 5분 초과")
    void cancelOrder_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();
        doThrow(new BusinessException(OrderErrorCode.CANCEL_TIMEOUT))
                .when(orderCancelFacade).cancelOrderByCustomer(eq(orderId), any());

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                        .with(user(customerPrincipal)).with(csrf()))
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
                        .with(user(ownerPrincipal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(document("order-reject-by-owner",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"))
                ));
        verify(orderCancelFacade, times(1)).rejectOrder(eq(orderId), any());

    }

    @Test
    @DisplayName("매니저용 주문 취소 성공")
    void cancelOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        mockMvc.perform(post("/api/orders/{orderId}/cancel/manager", orderId)
                        .with(user(managerPrincipal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(document("order-cancel-by-manager",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"))
                ));
        verify(orderCancelFacade, times(1)).cancelOrderByManager(eq(orderId));

    }


    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 수락")
    void acceptOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_ACCEPTED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .with(user(ownerPrincipal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(document("order-change-status-by-owner",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"))
                ));
        verify(orderService, times(1)).changeOrderStatusByOwner(any(), any(), any());

    }

    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 주문 조리 완료")
    void cookOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .with(user(ownerPrincipal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());
        verify(orderService, times(1)).changeOrderStatusByOwner(any(), any(), any());

    }

    @Test
    @DisplayName("사장님용 주문 상태 변경 성공 - 주문 완료")
    void completeOrderByOwner_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COMPLETED);
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .with(user(ownerPrincipal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(print());

        verify(orderService, times(1)).changeOrderStatusByOwner(any(), any(), any());

    }


    @Test
    @DisplayName("사장님용 주문 상태 변경 실패 - 변경할 수 없는 상태값")
    void cookOrderByOwner_Failure() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        doThrow(new BusinessException(OrderErrorCode.INVALID_STATUS_CHANGE))
                .when(orderService).changeOrderStatusByOwner(any(), any(), any());
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .with(user(ownerPrincipal)).with(csrf())
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
                        .with(user(managerPrincipal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andDo(document("order-change-status-by-manager",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 식별 ID (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("result").description("응답 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"))
                ));

        verify(orderService, times(1)).changeOrderStatusByManager(any(), eq(orderId));

    }

    @Test
    @DisplayName("매니저용 주문 상태 변경 성공 - 주문 조리 완료")
    void cookOrderByManager_Success() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderStatusRequest request = new OrderStatusRequest(OrderStatus.ORDER_COOKED);
        mockMvc.perform(patch("/api/orders/{orderId}/status/manager", orderId)
                        .with(user(managerPrincipal)).with(csrf())
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
                        .with(user(managerPrincipal)).with(csrf())
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
                        .with(user(managerPrincipal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value("O005"))
                .andExpect(jsonPath("$.message").value("변경 불가능한 주문 상태입니다."))
                .andDo(print());

    }

}

