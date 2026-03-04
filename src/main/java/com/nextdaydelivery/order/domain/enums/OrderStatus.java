package com.nextdaydelivery.order.domain.enums;

/**
 * 주문 상태 관리를 위한 ENUM -> 추후 파일 분리
 */
public enum OrderStatus {
    ORDER_REQUESTED,  // 주문 요청
    ORDER_REJECTED,   // 주문 거절
    ORDER_CANCELED,   // 주문 취소
    ORDER_ACCEPTED,   // 주문 접수
    ORDER_COMPLETED   // 주문 완료
}
