package com.nextdaydelivery.order.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/me")
    public CommonResponse<Slice<OrderListResponse>> getMyOrders(@RequestHeader("X-User-Id") Long userId,
                                                                @RequestParam(required = false) UUID cursor,
                                                                @RequestParam(required = false, defaultValue = "10") int size) {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .lastReadOrderId(cursor)
                .customerId(userId)
                .build();
        Slice<OrderListResponse> response = orderService.getOrdersByCustomer(request, size);
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @PostMapping("/search")
    public CommonResponse<Slice<OrderListResponse>> getAllOrders(
            @RequestBody OrderSearchRequest request,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Slice<OrderListResponse> response = orderService.getOrdersByManager(request, size);
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @GetMapping("/store/{storeId}")
    public CommonResponse<Slice<OrderListResponse>> getStoreOrders(@PathVariable UUID storeId,
                                                                   @RequestParam(required = false, defaultValue = "false") Boolean active,
                                                                   @RequestHeader("X-User-Id") Long userId,
                                                                   @RequestParam(required = false) UUID cursor,
                                                                   @RequestParam(required = false, defaultValue = "10") int size) {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .storeId(storeId)
                .lastReadOrderId(cursor)
                .status(active ? OrderStatus.getActiveStatus() : null)
                .build();
        Slice<OrderListResponse> response = orderService.getStoreOrders(storeId, request, userId, size);
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @PatchMapping("/{orderId}/status")
    public CommonResponse<Void> changeStatusByOwner(@PathVariable UUID orderId,
                                                    @RequestBody OrderStatusRequest request,
                                                    @RequestHeader("X-User-Id") Long userId) {
        orderService.changeOrderStatusByOwner(request, orderId, userId);
        return CommonResponse.onSuccess();
    }

    @PatchMapping("/{orderId}/status/manager")
    public CommonResponse<Void> changeStatusByManager(@PathVariable UUID orderId,
                                                      @RequestBody OrderStatusRequest request) {
        orderService.changeOrderStatusByManager(request, orderId);
        return CommonResponse.onSuccess();
    }

    @PostMapping("/{orderId}/reject")
    public CommonResponse<Void> rejectOrder(@PathVariable UUID orderId,
                                            @RequestHeader("X-User-Id") Long userId) {
        orderService.rejectOrder(orderId, userId);
        return CommonResponse.onSuccess();
    }

    @PostMapping("/{orderId}/cancel")
    public CommonResponse<Void> cancelOrder(@PathVariable UUID orderId,
                                            @RequestHeader("X-User-Id") Long userId) {
        orderService.cancelOrderByCustomer(orderId, userId);
        return CommonResponse.onSuccess();
    }

    @GetMapping("/{orderId}")
    public CommonResponse<OrderDetailResponse> getOrderDetails(@PathVariable UUID orderId,
                                                               @RequestHeader("X-User-Id") Long userId) {
        OrderDetailResponse response = orderService.getOrderDetail(orderId, userId);
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

}
