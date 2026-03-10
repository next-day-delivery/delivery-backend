package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.QCategory;
import com.nextdaydelivery.store.domain.entity.QStoreCategory;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StoreCategoryJpaRepositoryImpl implements StoreCategoryQuerydslRepository {

    private final JPAQueryFactory queryFactory;
    private final QStoreCategory storeCategory = QStoreCategory.storeCategory;
    private final QCategory category = QCategory.category;

    @Override
    public List<StoreCategory> findAllByStoreIn(List<Store> stores) {
        return queryFactory
                .selectFrom(storeCategory)
                .join(storeCategory.category, category).fetchJoin() // Category를 한 번에 가져옴
                .where(storeCategory.store.in(stores))
                .fetch();
    }
}
