package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;

public interface StoreCategoryService {
    void createStoreCategory(Store store, Category category);
}
