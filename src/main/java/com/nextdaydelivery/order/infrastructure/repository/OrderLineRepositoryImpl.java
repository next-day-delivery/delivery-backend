package com.nextdaydelivery.order.infrastructure.repository;

import com.nextdaydelivery.order.domain.entity.OrderLine;
import com.nextdaydelivery.order.domain.repository.OrderLineRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderLineRepositoryImpl implements OrderLineRepository {
    private final OrderLineJpaRepository jpaRepository;

    @Override
    public void saveAll(List<OrderLine> lines) {
        jpaRepository.saveAll(lines);
    }
}
