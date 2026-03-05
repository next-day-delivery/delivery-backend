package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJPARepository extends JpaRepository<Order, UUID> {
    @Query("Select o from Order o " +
            "join Store s " +
            "where o.orderId = :orderId and s.user.userId = :userId"
    )
    Optional<Order> findByIdAndOwnerId(@Param("orderId") UUID orderId, @Param("userId") Long userId);
}
