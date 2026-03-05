package com.nextdaydelivery.payment.presentation.dto.response;

import java.util.UUID;

public record PaymentConfirmResponse(
        UUID orderId
) {
}
