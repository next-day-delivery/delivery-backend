package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category newCategory) {
        return categoryJpaRepository.save(newCategory);
    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        return categoryJpaRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        return categoryJpaRepository.findByCategoryName(categoryName);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        // JpaRepository에 선언된 쿼리 메서드 호출
        return categoryJpaRepository.existsByCategoryName(categoryName);
    }

    @Override
    public List<Category> findAllById(Iterable<UUID> ids) {
        // JpaRepository의 기본 메서드 활용
        return categoryJpaRepository.findAllById(ids);
    }

    @Override
    public List<Category> findAll() {
        // JpaRepository의 기본 메서드 활용
        return categoryJpaRepository.findAll();
    }
}