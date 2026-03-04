package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findByCategoryName(String categoryName);

    Category save(Category newCategory);
}
