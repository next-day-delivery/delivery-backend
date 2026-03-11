package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepository {
    // 저장
    Store save(Store store);

    // 단건 조회
    Optional<Store> findById(UUID id);

    // 삭제
    void deleteById(UUID id);

    // 목록 조회 ( Search )
    List<Store> findAll();

    // 추가: 검색 및 페이징을 위한 메서드
    Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable);

    boolean existsByIdAndUserId(UUID storeId, Long userId);
}
