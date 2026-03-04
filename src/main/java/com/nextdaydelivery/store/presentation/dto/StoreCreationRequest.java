package com.nextdaydelivery.store.presentation.dto;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import java.util.List;

public record StoreCreationRequest(
        String name,
        String sigungu,
        String sido,
        String dong,
        String detailAddress,
        List<String> categoryNames // UUID 대신 카테고리 이름 리스트를 받음
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