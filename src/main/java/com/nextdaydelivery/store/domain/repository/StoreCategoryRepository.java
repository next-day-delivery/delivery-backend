package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;

public interface StoreCategoryRepository {
    StoreCategory save(StoreCategory storeCategory);

    boolean existsByStoreAndCategory(Store store, Category category);
}
