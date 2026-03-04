package com.nextdaydelivery.store.presentation.dto;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;

public record StoreCreationRequest(
        String name,
        String sigungu,
        String sido,
        String dong,
        String detailAddress
//        List<UUID> categoryIds
) {
    // 이후 User 기능 구현 시, User도 추가
    public Store toEntity(StoreAddress address) {
        return Store.builder()
                .name(this.name) // 또는 그냥 name
//                .user(user)
                .storeAddress(address)
                .detailAddress(this.detailAddress)
                .build();
    }
}