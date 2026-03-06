package com.nextdaydelivery.order.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.order.application.service.OrderService;
import com.nextdaydelivery.order.presentation.dto.request.ChangeOrderStatusRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<CommonResponse<Void>> changeStatusByOwner(@PathVariable UUID orderId,
                                                                    @RequestBody ChangeOrderStatusRequest request,
                                                                    @RequestHeader("X-User-Id") Long userId) {
        orderService.changeOrderStatusByOwner(request, orderId, userId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @PatchMapping("/{orderId}/status/manager")
    public ResponseEntity<CommonResponse<Void>> changeStatusByManager(@PathVariable UUID orderId,
                                                                      @RequestBody ChangeOrderStatusRequest request) {
        orderService.changeOrderStatusByManager(request, orderId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @PostMapping("/{orderId}/reject")
    public ResponseEntity<CommonResponse<Void>> rejectOrder(@PathVariable UUID orderId,
                                                            @RequestHeader("X-User-Id") Long userId) {
        orderService.rejectOrder(orderId, userId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelOrder(@PathVariable UUID orderId,
                                                            @RequestHeader("X-User-Id") Long userId) {
        orderService.cancelOrderByCustomer(orderId, userId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
