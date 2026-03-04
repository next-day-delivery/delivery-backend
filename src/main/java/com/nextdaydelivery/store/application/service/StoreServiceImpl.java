package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreAddressService storeAddressService;


    @Override
    public StoreCreationResponse createStore(StoreCreationRequest request) {
        // 가게 주소를 가게 주소 테이블에 저장하기, 이때 중복 체크 해야함
        StoreAddress storeAddress = storeAddressService.getOrCreateAddress(
                request.sigungu(),
                request.sido(),
                request.dong()
        );

        // 이 부분을 완성해야함.
        Store store = Store.builder()
                .storeAddress(storeAddress)
                .name(request.name())
                .detailAddress(request.detailAddress())
                .build();

        return StoreCreationResponse.from(store);
    }
}
