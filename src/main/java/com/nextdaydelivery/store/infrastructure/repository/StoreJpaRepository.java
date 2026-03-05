package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreQuerydslRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreJpaRepository extends JpaRepository<Store, UUID>, StoreQuerydslRepository {
}
