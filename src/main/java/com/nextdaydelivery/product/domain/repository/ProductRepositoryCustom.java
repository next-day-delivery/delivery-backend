package com.nextdaydelivery.product.domain.repository;

import com.nextdaydelivery.product.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductRepositoryCustom {

    Slice<Product> searchByConditions(String name,
                                      Integer minPrice,
                                      Integer maxPrice,
                                      UUID cursorId,
                                      Pageable pageable);
}
