package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // @Service 대신 @Repository 사용
@RequiredArgsConstructor
public class StoreCategoryRepositoryImpl implements StoreCategoryRepository {

    private final StoreCategoryJpaRepository storeCategoryJpaRepository;

    @Override
    public StoreCategory save(StoreCategory storeCategory) {
        return storeCategoryJpaRepository.save(storeCategory);
    }

    @Override
    public boolean existsByStoreAndCategory(Store store, Category category) {
        return storeCategoryJpaRepository.existsByStoreAndCategory(store, category);
    }

    @Override
    public List<StoreCategory> findAllByStore(Store store) {
        return storeCategoryJpaRepository.findAllByStore(store);
    }

    @Override
    public Optional<StoreCategory> findFirstByStore(Store store) {
        return storeCategoryJpaRepository.findFirstByStore(store);
    }

    @Override
    public void deleteByStore(Store store) {
        storeCategoryJpaRepository.deleteByStore(store);
    }
}
