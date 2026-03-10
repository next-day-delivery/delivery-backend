package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.UUID;

public interface StoreReviewService {

    int getStoreReviewCount(UUID storeId);

    double getStoreRatingAvg(UUID storeId);

    void plusReviewSummary(Store store, int rating);

    void minusReviewSummary(Store store, int rating);
}
