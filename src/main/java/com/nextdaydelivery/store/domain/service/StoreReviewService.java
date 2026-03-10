package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.UUID;

public interface StoreReviewService {

    int getStoreReviewCount(UUID storeId);

    double getStoreRatingAvg(UUID storeId);

    void plusReviewSummary(Store store, int rating);

    void updateReviewSummary(Store store, int oldRating, int newRating);

    void minusReviewSummary(Store store, int rating);
}
