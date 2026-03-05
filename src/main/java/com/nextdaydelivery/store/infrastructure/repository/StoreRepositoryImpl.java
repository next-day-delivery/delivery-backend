package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private final StoreJpaRepository storeJpaRepository;

    @Override
    public Store save(Store store) {
        return storeJpaRepository.save(store);
    }

    @Override
    public Optional<Store> findById(UUID id) {
        return storeJpaRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        storeJpaRepository.deleteById(id);
    }

    @Override
    public List<Store> findAll() {
        return storeJpaRepository.findAll();
    }

//    @Override
//    public Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable) {
//        // QueryDSL을 이용한 동적 쿼리 로직이 들어가는 부분입니다.
//        // 현재는 예시로 기본 페이징 조회를 보여드립니다.
//        return storeJpaRepository.findAll(pageable);
//    }

    @Override
    public Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable) {
        // 이제 storeJpaRepository가 StoreRepositoryCustom을 상속받았으므로
        // 직접 호출이 가능합니다. 실제 로직은 StoreRepositoryCustomImpl에서 실행됩니다.
        return storeJpaRepository.searchStores(condition, pageable);
    }
}
