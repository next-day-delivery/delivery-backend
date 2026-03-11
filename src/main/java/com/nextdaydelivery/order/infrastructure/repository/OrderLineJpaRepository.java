package com.nextdaydelivery.order.infrastructure.repository;

import com.nextdaydelivery.order.domain.entity.OrderLine;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineJpaRepository extends JpaRepository<OrderLine, UUID> {
}
