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
        // 1. 기존에 연결된 모든 StoreCategory 삭제 (Delete-and-Insert 전략)
        // Store 엔티티 내의 storeCategories 리스트도 비워줘야 OrphanRemoval이 정상 작동하거나
        // 직접 Repository에서 삭제 쿼리를 날릴 수 있습니다.
        storeCategoryRepository.deleteByStore(store);

        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 2. 새로운 카테고리 ID 목록으로 다시 생성
        for (UUID categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

            createStoreCategory(store, category);
        }
    }
}
