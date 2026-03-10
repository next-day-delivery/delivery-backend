package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreQuerydslRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreJpaRepository extends JpaRepository<Store, UUID>, StoreQuerydslRepository {
    @Query("SELECT COUNT(s) > 0 FROM Store s WHERE s.storeId = :storeId AND s.user.userId = :userId AND s.deletedAt is null")
    boolean existsByIdAndUserId(@Param("storeId") UUID storeId, @Param("userId") Long userId);
}
