package com.nextdaydelivery.delivery.presentation.dto.request;

import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;

public record DeliveryStatusRequest(
        @NotNull(message = "배송 상태값은 필수입니다.")
        DeliveryStatus status
) {
}
