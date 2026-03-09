package com.nextdaydelivery.store.domain.service;

import java.util.UUID;

public interface StoreReviewService {

    int getStoreReviewCount(UUID storeId);

    double getStoreRatingAvg(UUID storeId);
}
