package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import lombok.RequiredArgsConstructor;
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
        // 빌더를 사용하여 중간 테이블 엔티티 생성
        StoreCategory storeCategory = StoreCategory.builder()
                .store(store)
                .category(category)
                .build();

        // 도메인 레포지토리를 통해 저장
        storeCategoryRepository.save(storeCategory);
    }
}
