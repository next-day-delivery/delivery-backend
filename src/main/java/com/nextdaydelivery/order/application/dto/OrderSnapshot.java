package com.nextdaydelivery.order.application.dto;

import com.nextdaydelivery.checkout.presentation.dto.request.CheckoutRequest;
import java.util.List;
import java.util.UUID;

public record OrderSnapshot(
        UUID cartId,
        Long userId,
        UUID storeId,
        Long amount,
        String address,
        List<OrderItemSnapshot> items
) {
    public static OrderSnapshot from(CheckoutRequest request, Long userId) {
        List<OrderItemSnapshot> itemSnapshots = request.items().stream()
                .map(item -> new OrderItemSnapshot(
                        item.productId(),
                        item.productName(), // 명칭이 포함되어 있다고 가정
                        item.quantity(),
                        item.price()
                ))
                .toList();

        return new OrderSnapshot(
                request.cartId(),
                userId,
                request.storeId(),
                request.amount(),
                request.address(),
                itemSnapshots
        );
    }

    public record OrderItemSnapshot(
            UUID productId,
            String productName,
            Integer quantity,
            Long price
    ) {
    }
}
