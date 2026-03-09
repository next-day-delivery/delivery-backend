package com.nextdaydelivery.product.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProductUpdateRequest(
        @NotNull UUID productId,
        String productDetail,
        String productName,
        Integer price
) {
}
