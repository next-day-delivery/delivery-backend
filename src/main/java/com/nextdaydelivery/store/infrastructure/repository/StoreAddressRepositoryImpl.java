package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreAddressRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreAddressRepositoryImpl implements StoreAddressRepository {
    private final StoreAddressJpaRepository storeAddressJpaRepository;

    @Override
    public StoreAddress save(StoreAddress storeAddress) {
        return storeAddressJpaRepository.save(storeAddress);
    }

    @Override
    public List<StoreAddress> findAll() {
        return storeAddressJpaRepository.findAll();
    }

    @Override
    public Optional<StoreAddress> findBySigunguAndSidoAndDong(String sigungu, String sido, String dong) {
        return storeAddressJpaRepository.findBySigunguAndSidoAndDong(sigungu, sido, dong);
    }
}
