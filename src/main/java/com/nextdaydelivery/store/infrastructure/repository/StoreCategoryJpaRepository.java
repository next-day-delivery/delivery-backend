package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryJpaRepository extends JpaRepository<StoreCategory, UUID> {
    boolean existsByStoreAndCategory(Store store, Category category);

    // 추가해야 할 메서드들
    List<StoreCategory> findAllByStore(Store store);

    Optional<StoreCategory> findFirstByStore(Store store);

    void deleteByStore(Store store);
}
