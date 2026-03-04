package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // @Service 대신 @Repository 사용
@RequiredArgsConstructor
public class StoreCategoryRepositoryImpl implements StoreCategoryRepository {

    // 도메인 인터페이스가 아닌, JPA 인터페이스를 주입받아야 합니다.
    private final StoreCategoryJpaRepository storeCategoryJpaRepository;

    @Override
    public StoreCategory save(StoreCategory storeCategory) {
        // JPA의 save 메서드를 호출하여 실제 DB에 영속화합니다.
        return storeCategoryJpaRepository.save(storeCategory);
    }
}