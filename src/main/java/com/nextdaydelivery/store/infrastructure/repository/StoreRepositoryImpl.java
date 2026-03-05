package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
        return storeJpaRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        storeJpaRepository.deleteById(id);
    }

    @Override
    public List<Store> findAll() {
        return storeJpaRepository.findAll();
    }
}
