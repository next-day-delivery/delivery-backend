package com.nextdaydelivery.delivery.infrastructure;

import com.nextdaydelivery.delivery.domain.entity.Delivery;
import com.nextdaydelivery.delivery.domain.repository.DeliveryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository {
    private final DeliveryJpaRepository jpaRepository;

    @Override
    public Delivery save(Delivery delivery) {
        return jpaRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> findByIdWithLock(UUID deliveryId) {
        return jpaRepository.findByIdWithLock(deliveryId);
    }

    @Override
    public Optional<Delivery> findByIdWithDetails(UUID deliveryId) {
        return jpaRepository.findByIdWithDetails(deliveryId);
    }
}
