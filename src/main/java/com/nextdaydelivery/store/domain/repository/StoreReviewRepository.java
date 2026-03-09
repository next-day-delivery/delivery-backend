package com.nextdaydelivery.store.domain.repository;

import java.util.UUID;

public interface StoreReviewRepository {

    //가게 리뷰 수 조회
    int getStoreReviewCount(UUID storeId);

    //가게 평점 조회
    double getStoreRatingAvg(UUID storeId);
}
