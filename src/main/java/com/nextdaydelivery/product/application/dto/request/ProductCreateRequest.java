package com.nextdaydelivery.product.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record ProductCreateRequest(
        @NotNull UUID storeId,
        @NotBlank String productName,
        String productDetail,
        @Positive int price,
        boolean useAi
) {
}
