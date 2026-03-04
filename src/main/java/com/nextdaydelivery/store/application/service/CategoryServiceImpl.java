package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.repository.CategoryRepository;
import com.nextdaydelivery.store.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category getOrCreateCategory(String categoryName) {
        // 1. 이름으로 기존 카테고리 조회
        // 2. 없으면 빌더를 통해 생성 후 저장(save) 및 반환
        return categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .categoryName(categoryName)
                                .build()
                ));
    }
}
