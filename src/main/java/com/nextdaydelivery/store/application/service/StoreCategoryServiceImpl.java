package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreCategoryServiceImpl implements StoreCategoryService {

    private final StoreCategoryRepository storeCategoryRepository;

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
}
