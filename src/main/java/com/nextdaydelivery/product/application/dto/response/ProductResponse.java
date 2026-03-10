package com.nextdaydelivery.product.application.dto.response;

import com.nextdaydelivery.product.domain.entity.Product;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String productDetail,
        Integer price
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getProductId(), p.getProductName(), p.getProductDetail(), p.getPrice());
    }
}
