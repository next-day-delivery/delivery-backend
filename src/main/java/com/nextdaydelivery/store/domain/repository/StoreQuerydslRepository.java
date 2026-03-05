package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreQuerydslRepository {
    // 우리가 만들 복잡한 검색 메서드
    Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable);
}