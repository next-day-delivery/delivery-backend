package com.nextdaydelivery.delivery.infrastructure;

import com.nextdaydelivery.delivery.domain.entity.Delivery;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryJpaRepository extends JpaRepository<Delivery, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select d from Delivery d " +
            "where d.deliveryId = :deliveryId"
    )
    Optional<Delivery> findByIdWithLock(@Param("deliveryId") UUID deliveryId);


    @Query("SELECT d FROM Delivery d " +
            "JOIN FETCH d.order o JOIN FETCH o.store s JOIN FETCH s.user u " +
            "WHERE d.deliveryId = :deliveryId")
    Optional<Delivery> findByIdWithDetails(@Param("deliveryId") UUID deliveryId);
}
