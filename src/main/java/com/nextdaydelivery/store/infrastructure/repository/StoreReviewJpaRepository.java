package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReviewJpaRepository extends JpaRepository<Store, UUID> {
    int findReviewCountByStoreId(UUID storeId);

    double findRatingAvgByStoreId(UUID storeId);
}
