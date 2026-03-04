package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.domain.entity.StoreAddress;

public interface StoreAddressService {
    StoreAddress getOrCreateAddress(String sigungu, String sido, String dong);
}
