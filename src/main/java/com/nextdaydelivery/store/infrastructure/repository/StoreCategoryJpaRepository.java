package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryQuerydslRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryJpaRepository extends JpaRepository<StoreCategory, UUID>,
        StoreCategoryQuerydslRepository {
    boolean existsByStoreAndCategory(Store store, Category category);

    List<StoreCategory> findAllByStore(Store store);

    Optional<StoreCategory> findFirstByStore(Store store);

    void deleteByStore(Store store);

    List<StoreCategory> findAllByStoreIn(List<Store> stores);
}
