package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
}
