package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.StoreAddress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreAddressJpaRepository extends JpaRepository<StoreAddress, Long> {
    Optional<StoreAddress> findBySigunguAndSidoAndDong(String sigungu, String sido, String dong);
}
