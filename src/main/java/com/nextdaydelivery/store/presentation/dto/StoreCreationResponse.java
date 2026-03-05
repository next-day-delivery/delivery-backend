package com.nextdaydelivery.store.presentation.dto;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.List;
import java.util.UUID;

public record StoreCreationResponse(
        UUID storeId,
        String name,
        String sigungu,
        String sido,
        String dong,
        String detailAddress,
        List<UUID> categoryIds
) {
    // 이후 User 기능 구현 시, User도 추가
    public static StoreCreationResponse from(Store store, List<UUID> categoryIds) {
        return new StoreCreationResponse(
                store.getStoreId(),
                store.getName(),
                store.getStoreAddress().getSigungu(),
                store.getStoreAddress().getSido(),
                store.getStoreAddress().getDong(),
                store.getDetailAddress(),
                categoryIds
        );
    }
}