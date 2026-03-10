package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import java.util.List;

public interface StoreCategoryQuerydslRepository {
    List<StoreCategory> findAllByStoreIn(List<Store> stores);
}
