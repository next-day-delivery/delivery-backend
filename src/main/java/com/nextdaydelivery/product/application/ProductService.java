package com.nextdaydelivery.product.application;

import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse create(ProductCreateRequest createRequest);
    ProductResponse update(ProductUpdateRequest updateRequest);
    List<ProductResponse> readAll();
    ProductResponse readById(UUID id);
    UUID deleteById(UUID id);
    ProductResponse hideById(UUID id);
}
