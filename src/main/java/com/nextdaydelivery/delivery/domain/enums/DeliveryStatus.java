package com.nextdaydelivery.delivery.domain.enums;

/**
 * 배달 상태 관리를 위한 ENUM
 */
public enum DeliveryStatus {
    DELIVERY_PENDING,   // 배달 대기
    DELIVERY_ING,       // 배달 중
    DELIVERY_COMPLETED;  // 배달 완료

    public boolean canChangeTo(DeliveryStatus status) {

        if (status == null) {
            return false;
        }
        return switch (this) {
            case DELIVERY_PENDING -> status == DELIVERY_ING;
            case DELIVERY_ING -> status == DELIVERY_COMPLETED;
            default -> false;
        };

    }
}
