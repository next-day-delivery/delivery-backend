package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import java.util.List;
import java.util.UUID;

public interface StoreCategoryService {
    void createStoreCategory(Store store, Category category);

    // 추가: 기존 카테고리 관계를 지우고 새로 등록하는 로직
    void updateStoreCategories(Store store, List<UUID> categoryIds);
}