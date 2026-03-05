package com.nextdaydelivery.store.presentation.dto;

import com.nextdaydelivery.store.domain.entity.Store;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StoreResponse(
        UUID storeId,
        String name,
        String ownerNickname,  // User 엔티티에서 추출
        String fullAddress,    // 시도 + 시군구 + 동 + 상세주소 조합
        BigDecimal ratingAvg,      // 평점
        Integer reviewCount,   // 리뷰 수
        List<String> categoryNames, // ID가 아닌 실제 이름 ("치킨", "피자")
        LocalDateTime createdAt
) {
    public static StoreResponse from(Store store, String ownerNickname, List<String> categoryNames) {
        return new StoreResponse(
                store.getStoreId(),
                store.getName(),
                ownerNickname,
                String.format("%s %s %s %s",
                        store.getStoreAddress().getSido(),
                        store.getStoreAddress().getSigungu(),
                        store.getStoreAddress().getDong(),
                        store.getDetailAddress()),
                store.getRatingAvg(),
                store.getReviewCount(),
                categoryNames,
                store.getCreatedAt()
        );
    }
}