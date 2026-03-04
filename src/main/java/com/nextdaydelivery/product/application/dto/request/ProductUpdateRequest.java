package com.nextdaydelivery.product.application.dto.request;

import java.util.UUID;

public record ProductUpdateRequest(
        UUID id,
        String productDetail,
        String productName,
        Integer price
) {
}
