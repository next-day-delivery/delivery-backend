package com.nextdaydelivery.order.domain.repository;

import com.nextdaydelivery.order.domain.entity.OrderLine;
import java.util.List;

public interface OrderLineRepository {
    void saveAll(List<OrderLine> lines);
}
