package com.nextdaydelivery.store.presentation.dto;

import com.nextdaydelivery.store.domain.entity.Store;
import java.math.BigDecimal;
import java.util.UUID;

public record StoreListResponse(
        UUID storeId,
        String name,
        String region,        // "서울 종로구" 정도의 요약 주소
        BigDecimal ratingAvg,
        Integer reviewCount,
        String mainCategory   // 여러 개 중 대표 카테고리 하나만 노출
) {
    public static StoreListResponse from(Store store, String mainCategory) {
        return new StoreListResponse(
                store.getStoreId(),
                store.getName(),
                store.getStoreAddress().getSido() + " " + store.getStoreAddress().getSigungu(),
                store.getRatingAvg(),
                store.getReviewCount(),
                mainCategory
        );
    }
}