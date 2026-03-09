package com.nextdaydelivery.product.application.dto.response;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String productDetail,
        Integer price
) {
}
