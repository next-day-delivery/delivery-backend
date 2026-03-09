package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.Category;

public interface CategoryService {
    // 반환 타입을 엔티티로 변경
    Category getOrCreateCategory(String categoryName);
}
