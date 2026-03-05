package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository {
    // 저장
    Store save(Store store);

    // 단건 조회
    Optional<Store> findById(UUID id);

    // 삭제
    void deleteById(UUID id);

    // 목록 조회 ( Search )
    List<Store> findAll();
}
