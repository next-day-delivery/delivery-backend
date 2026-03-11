package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private final StoreJpaRepository storeJpaRepository;

    @Override
    public Store save(Store store) {
        return storeJpaRepository.save(store);
    }

    @Override
    public Optional<Store> findById(UUID id) {
        return storeJpaRepository.findById(id)
                .filter(store -> store.getDeletedAt() == null);
    }

    @Override
    public void deleteById(UUID id) {
        storeJpaRepository.deleteById(id);
    }

    @Override
    public List<Store> findAll() {
        return storeJpaRepository.findAll();
    }

    @Override
    public Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable) {
        // 이제 storeJpaRepository가 StoreRepositoryCustom을 상속받았으므로
        // 직접 호출이 가능합니다. 실제 로직은 StoreRepositoryCustomImpl에서 실행됩니다.
        return storeJpaRepository.searchStores(condition, pageable);
    }

    @Override
    public boolean existsByIdAndUserId(UUID storeId, Long userId) {
        return storeJpaRepository.existsByIdAndUserId(storeId, userId);
    }
}
