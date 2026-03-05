package com.nextdaydelivery.store.presentation.controller;

public interface StoreControllerApi {
    // 1. 가게 평점 (별점 평균) 저장: 가게 테이블에 저장
    void ratingAvgPost();

    // 2. 리뷰 수 저장
    void reviewCountSave();

    // 3. 리뷰 수 가져오기
    void reviewCountGet();

    // 4. 별점 평균 가져오기
    void ratingAvgGet();
}
