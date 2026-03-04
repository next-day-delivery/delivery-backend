package com.nextdaydelivery.product.application.dto.request;

import java.util.UUID;

public record ProductCreateRequest(
        UUID storeId,
        String productName,
        String productDetail,
        int price,
        boolean useAi
) {
}
