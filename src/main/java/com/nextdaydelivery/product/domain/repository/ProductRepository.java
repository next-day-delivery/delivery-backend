package com.nextdaydelivery.product.domain.repository;

import com.nextdaydelivery.product.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(UUID id);

    Slice<Product> searchByConditions(String name,
                                      Integer minPrice,
                                      Integer maxPrice,
                                      UUID cursorId,
                                      UUID storeId,
                                      Pageable pageable);


    List<Product> findAll();

    void delete(Product product);
}
