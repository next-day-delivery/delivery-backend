package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.repository.StoreReviewRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreReviewRepositoryImpl implements StoreReviewRepository {

    private final StoreReviewJpaRepository storeReviewJpaRepository;

    @Override
    public int getStoreReviewCount(UUID storeId) {
        return storeReviewJpaRepository.findReviewCountByStoreId(storeId);
    }

    @Override
    public double getStoreRatingAvg(UUID storeId) {
        return storeReviewJpaRepository.findRatingAvgByStoreId(storeId);
    }
}
