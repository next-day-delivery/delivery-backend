package com.nextdaydelivery.order.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.annotation.RequireManagerRole;
import com.nextdaydelivery.global.security.annotation.RequireOwnerRole;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.order.application.fascade.OrderCancelFacade;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderReviewStatusResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderCancelFacade orderCancelFacade;

    @RequireCustomerRole
    @GetMapping("/me")
    public CommonResponse<Slice<OrderListResponse>> getMyOrders(@AuthenticationPrincipal PrincipalDetails details,
                                                                @RequestParam(required = false) UUID cursor,
                                                                Pageable pageable) {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .lastReadOrderId(cursor)
                .customerId(details.getAuthUserDto().userId())
                .build();
        Slice<OrderListResponse> response = orderService.getOrdersByCustomer(request, pageable.getPageSize());
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @RequireManagerRole
    @PostMapping("/search")
    public CommonResponse<Slice<OrderListResponse>> getAllOrders(
            @RequestBody OrderSearchRequest request,
            Pageable pageable) {
        Slice<OrderListResponse> response = orderService.getOrdersByManager(request, pageable.getPageSize());
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @RequireOwnerRole
    @GetMapping("/store/{storeId}")
    public CommonResponse<Slice<OrderListResponse>> getStoreOrders(@PathVariable UUID storeId,
                                                                   @RequestParam(required = false, defaultValue = "false") Boolean active,
                                                                   @AuthenticationPrincipal PrincipalDetails details,
                                                                   @RequestParam(required = false) UUID cursor,
                                                                   Pageable pageable) {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .storeId(storeId)
                .lastReadOrderId(cursor)
                .status(active ? OrderStatus.getActiveStatus() : null)
                .build();
        Slice<OrderListResponse> response = orderService.getStoreOrders(storeId, request, details.getAuthUserDto()
                .userId(), pageable.getPageSize());
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @RequireOwnerRole
    @PatchMapping("/{orderId}/status")
    public CommonResponse<Void> changeStatusByOwner(@PathVariable UUID orderId,
                                                    @RequestBody OrderStatusRequest request,
                                                    @AuthenticationPrincipal PrincipalDetails details) {
        orderService.changeOrderStatusByOwner(request, orderId, details.getAuthUserDto().userId());
        return CommonResponse.onSuccess();
    }

    @RequireManagerRole
    @PatchMapping("/{orderId}/status/manager")
    public CommonResponse<Void> changeStatusByManager(@PathVariable UUID orderId,
                                                      @RequestBody OrderStatusRequest request) {
        orderService.changeOrderStatusByManager(request, orderId);
        return CommonResponse.onSuccess();
    }

    @RequireOwnerRole
    @PostMapping("/{orderId}/reject")
    public CommonResponse<Void> rejectOrder(@PathVariable UUID orderId,
                                            @AuthenticationPrincipal PrincipalDetails details) {
        orderCancelFacade.rejectOrder(orderId, details.getAuthUserDto().userId());
        return CommonResponse.onSuccess();
    }

    @RequireCustomerRole
    @PostMapping("/{orderId}/cancel")
    public CommonResponse<Void> cancelOrderByCustomer(@PathVariable UUID orderId,
                                                      @AuthenticationPrincipal PrincipalDetails details) {
        orderCancelFacade.cancelOrderByCustomer(orderId, details.getAuthUserDto().userId());
        return CommonResponse.onSuccess();
    }

    @RequireManagerRole
    @PostMapping("/{orderId}/cancel/manager")
    public CommonResponse<Void> cancelOrderByManager(@PathVariable UUID orderId) {
        orderCancelFacade.cancelOrderByManager(orderId);
        return CommonResponse.onSuccess();
    }

    @GetMapping("/{orderId}")
    public CommonResponse<OrderDetailResponse> getOrderDetails(@PathVariable UUID orderId,
                                                               @AuthenticationPrincipal PrincipalDetails details) {
        OrderDetailResponse response = orderService.getOrderDetail(orderId, details.getAuthUserDto().userId(),
                details.getAuthUserDto().role());
        return CommonResponse.onSuccess(HttpStatus.OK, response);
    }

    @GetMapping("/{orderId}/review")
    public CommonResponse<OrderReviewStatusResponse> getReviewStatus(
            @PathVariable UUID orderId
    ) {
        return CommonResponse.onSuccess(orderService.getReviewStatus(orderId));
    }

}
