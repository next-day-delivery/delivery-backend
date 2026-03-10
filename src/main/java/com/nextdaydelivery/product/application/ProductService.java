package com.nextdaydelivery.product.application;

import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductService {
    ProductResponse create(ProductCreateRequest createRequest);

    ProductResponse update(ProductUpdateRequest updateRequest);

    ProductResponse readById(UUID id);

    UUID deleteById(UUID id);

    ProductResponse hideById(UUID id);

    Slice<ProductResponse> searchProducts(String name,
                                          Integer minPrice,
                                          Integer maxPrice,
                                          UUID cursorId,
                                          UUID storeId,
                                          Pageable pageable);
}
