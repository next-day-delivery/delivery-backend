package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.repository.CategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;


    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        return categoryJpaRepository.findByCategoryName(categoryName);
    }

    @Override
    public Category save(Category newCategory) {
        return categoryJpaRepository.save(newCategory);
    }
}
