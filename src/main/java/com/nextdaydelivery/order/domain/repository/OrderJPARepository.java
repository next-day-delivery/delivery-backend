package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.Order;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJPARepository extends JpaRepository<Order, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)    // 사용자,매니저랑 동시에 상태 변경을 진행할 경우 대비
    @Query("Select o from Order o " +
            "join o.store s " +
            "where o.orderId = :orderId and s.user.userId = :userId"
    )
    Optional<Order> findByIdAndOwnerId(@Param("orderId") UUID orderId, @Param("userId") Long userId);
}
