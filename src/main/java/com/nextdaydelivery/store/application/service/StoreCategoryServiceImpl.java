package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.CategoryRepository;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreCategoryServiceImpl implements StoreCategoryService {

    private final StoreCategoryRepository storeCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional // 부모 서비스에서 트랜잭션이 시작되었겠지만, 안전을 위해 붙여줍니다.
    public void createStoreCategory(Store store, Category category) {
        if (storeCategoryRepository.existsByStoreAndCategory(store, category)) {
            return;
        }
        try {
            StoreCategory storeCategory = StoreCategory.builder()
                    .store(store)
                    .category(category)
                    .build();
            storeCategoryRepository.save(storeCategory);
        } catch (DataIntegrityViolationException ex) {
            // 동시성 경합으로 이미 생성된 경우 멱등 처리
            if (!storeCategoryRepository.existsByStoreAndCategory(store, category)) {
                throw ex;
            }
        }
    }

    @Override
    @Transactional
    public void updateStoreCategories(Store store, List<UUID> categoryIds) {
        // 1. 기존 연결 삭제
        storeCategoryRepository.deleteByStore(store);

        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 2. 한 번의 쿼리로 모든 카테고리 조회 (SELECT ... FROM category WHERE id IN (...))
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        // 3. 입력받은 ID 개수와 조회된 개수가 다른지 체크 (예외 처리)
        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("일부 카테고리 ID가 유효하지 않습니다.");
        }

        // 4. 생성 및 저장
        for (Category category : categories) {
            createStoreCategory(store, category);
        }
    }
}
