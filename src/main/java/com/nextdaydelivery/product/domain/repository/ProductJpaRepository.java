package com.nextdaydelivery.product.domain.repository;

import com.nextdaydelivery.product.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
