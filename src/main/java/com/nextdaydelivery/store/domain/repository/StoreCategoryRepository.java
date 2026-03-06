package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import java.util.List;
import java.util.Optional;

public interface StoreCategoryRepository {

    // 저장
    StoreCategory save(StoreCategory storeCategory);

    // 중복 체크 (생성 시 사용)
    boolean existsByStoreAndCategory(Store store, Category category);

    // 가게별 모든 카테고리 연결 정보 조회 (상세 조회 시 사용)
    List<StoreCategory> findAllByStore(Store store);

    // 가게의 첫 번째 카테고리만 조회 (목록 조회 시 대표 카테고리용)
    Optional<StoreCategory> findFirstByStore(Store store);

    // 삭제 (수정/삭제 시 사용)
    void deleteByStore(Store store);
}
