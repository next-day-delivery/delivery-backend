package com.nextdaydelivery.order.domain.entity.enums;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 주문 상태 관리를 위한 ENUM -> 추후 파일 분리
 */
@RequiredArgsConstructor
public enum OrderStatus {
    ORDER_REQUESTED("주문 요청"),  // 주문 요청
    ORDER_REJECTED("주문 거절"),   // 주문 거절
    ORDER_CANCELED("주문 취소"),   // 주문 취소
    ORDER_ACCEPTED("주문 수락"),   // 주문 접수
    ORDER_COOKED("주문 조리 완료"),
    ORDER_COMPLETED("주문 완료"); // 주문 완료

    private final String description;

    public boolean canChangeTo(OrderStatus status) {
        if (status == null) {
            return false;
        }
        return switch (this) {
            case ORDER_REQUESTED -> List.of(ORDER_ACCEPTED, ORDER_CANCELED, ORDER_REJECTED).contains(status);
            case ORDER_ACCEPTED -> List.of(ORDER_CANCELED, ORDER_COOKED).contains(status);
            case ORDER_COOKED -> status == ORDER_COMPLETED;
            default -> false;
        };
    }

    public static List<OrderStatus> getActiveStatus() {
        return List.of(ORDER_REQUESTED, ORDER_ACCEPTED, ORDER_COOKED);
    }
}
