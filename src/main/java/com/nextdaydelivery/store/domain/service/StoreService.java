package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;

public interface StoreService {

    StoreCreationResponse createStore(StoreCreationRequest request);
}
