package com.nextdaydelivery.delivery.domain.repository;

import com.nextdaydelivery.delivery.domain.entity.Delivery;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {
    Delivery save(Delivery delivery);

    Optional<Delivery> findByIdWithLock(UUID deliveryId);

    Optional<Delivery> findByIdWithDetailsAndLock(UUID deliveryId);

}
