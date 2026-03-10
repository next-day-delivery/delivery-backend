package com.nextdaydelivery.order.presentation.dto.response;

import java.time.LocalDateTime;

public record OrderReviewStatusResponse(boolean reviewed, LocalDateTime reviewedAt) {
}
