package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.StoreAddress;
import java.util.List;
import java.util.Optional;

public interface StoreAddressRepository {
    // 저장
    StoreAddress save(StoreAddress storeAddress);

    // 목록 조회
    List<StoreAddress> findAll();

    Optional<StoreAddress> findBySigunguAndSidoAndDong(String sigungu, String sido, String dong);
}
