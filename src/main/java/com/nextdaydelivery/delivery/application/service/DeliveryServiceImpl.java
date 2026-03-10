package com.nextdaydelivery.delivery.application.service;

import com.nextdaydelivery.delivery.domain.entity.Delivery;
import com.nextdaydelivery.delivery.domain.enums.DeliveryStatus;
import com.nextdaydelivery.delivery.domain.event.DeliveryCompletedEvent;
import com.nextdaydelivery.delivery.domain.repository.DeliveryRepository;
import com.nextdaydelivery.global.domain.error.DeliveryErrorCode;
import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void updateDeliveryStatusByOwner(UUID deliveryId, DeliveryStatus status, Long userId) {
        Delivery delivery = findByIdWithDetails(deliveryId);
        validateOwner(delivery, userId);
        findByIdWithLock(deliveryId);
        delivery.transitionTo(status);
        publishEventIfCompleted(delivery);
    }

    @Override
    @Transactional
    public void updateDeliveryStatusByManager(UUID deliveryId, DeliveryStatus status) {
        Delivery delivery = findByIdWithLock(deliveryId);
        delivery.forceUpdateStatus(status);
        publishEventIfCompleted(delivery);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
        Delivery delivery = Delivery.createDelivery(order);
        deliveryRepository.save(delivery);
    }

    private void validateOwner(Delivery delivery, Long userId) {
        Long ownerId = delivery.getOrder().getStore().getUser().getUserId();
        if (!ownerId.equals(userId)) {
            throw new BusinessException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
        }
    }

    private Delivery findByIdWithLock(UUID deliveryId) {
        return deliveryRepository.findByIdWithLock(deliveryId)
                .orElseThrow(() -> new BusinessException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
    }

    private Delivery findByIdWithDetails(UUID deliveryId) {
        return deliveryRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new BusinessException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

    }

    private void publishEventIfCompleted(Delivery delivery) {
        if (delivery.getDeliveryStatus() == DeliveryStatus.DELIVERY_COMPLETED) {
            applicationEventPublisher.publishEvent(new DeliveryCompletedEvent(delivery.getOrder().getOrderId()));
        }
    }
}
