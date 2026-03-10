package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreReviewRepository;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreReviewServiceImpl implements StoreReviewService {

    private final StoreReviewRepository storeReviewRepository;

    @Override
    public int getStoreReviewCount(UUID storeId) {
        return storeReviewRepository.getStoreReviewCount(storeId);
    }

    @Override
    public double getStoreRatingAvg(UUID storeId) {
        return storeReviewRepository.getStoreRatingAvg(storeId);
    }

    @Override
    public void plusReviewSummary(Store store, int rating) {
        store.addReview(rating);
    }

    @Override
    public void minusReviewSummary(Store store, int rating) {
        store.removeReview(rating);
    }
}
