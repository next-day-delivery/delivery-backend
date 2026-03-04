package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreAddressRepository;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreAddressServiceImpl implements StoreAddressService {
    private final StoreAddressRepository storeAddressRepository;

    @Transactional
    public StoreAddress getOrCreateAddress(String sigungu, String sido, String dong) {
        return storeAddressRepository.findBySigunguAndSidoAndDong(sigungu, sido, dong)
                .orElseGet(() -> storeAddressRepository.save(
                        StoreAddress.builder()
                                .sigungu(sigungu)
                                .sido(sido)
                                .dong(dong)
                                .build()
                ));
    }
}