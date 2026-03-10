package com.nextdaydelivery.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.checkout.domain.entity.Checkout;
import com.nextdaydelivery.global.config.PaginationConfig;
import com.nextdaydelivery.global.domain.error.OrderErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.order.application.dto.OrderSnapshot;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.entity.OrderLine;
import com.nextdaydelivery.order.domain.enums.OrderStatus;
import com.nextdaydelivery.order.domain.repository.OrderLineRepository;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.order.domain.repository.dto.OrderDetails;
import com.nextdaydelivery.order.domain.repository.dto.OrderSearchCritera;
import com.nextdaydelivery.order.domain.repository.dto.OrderSlice;
import com.nextdaydelivery.order.presentation.dto.request.OrderSearchRequest;
import com.nextdaydelivery.order.presentation.dto.request.OrderStatusRequest;
import com.nextdaydelivery.order.presentation.dto.response.OrderDetailResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderListResponse;
import com.nextdaydelivery.order.presentation.dto.response.OrderReviewStatusResponse;
import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.repository.ProductRepository;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.user.domain.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaginationConfig paginationConfig;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final OrderLineRepository orderLineRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Slice<OrderListResponse> getOrdersByCustomer(OrderSearchRequest request, int size) {
        //유효한 유저인지 검증
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(OrderSearchCritera.from(request),
                validatedPageSize);
        return orderSlices.map(slice -> OrderListResponse.ofCustomer(slice, request.customerId()));
    }

    @Override
    public Slice<OrderListResponse> getOrdersByManager(OrderSearchRequest request, int size) {
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(OrderSearchCritera.from(request),
                validatedPageSize);
        return orderSlices.map(OrderListResponse::from);
    }

    @Override
    public Slice<OrderListResponse> getStoreOrders(UUID storeId, OrderSearchRequest request, Long userId, int size) {
        //유저가 해당 가게 사장인지 검증
        int validatedPageSize = paginationConfig.getValidatedSize(size);
        Slice<OrderSlice> orderSlices = orderRepository.searchOrders(OrderSearchCritera.from(request),
                validatedPageSize);
        return orderSlices.map(OrderListResponse::from);

    }

    @Override
    public OrderDetailResponse getOrderDetail(UUID orderId, Long userId) {
        OrderDetails details = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
        validateOrderAccess(details);
        return OrderDetailResponse.from(details, userId);
    }

    @Transactional
    @Override
    public void cancelOrderByCustomer(UUID orderId, Long userId) {
        Order order = getOrderByCustomerIdWithLock(orderId, userId);
        order.validateCancelableTime();
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void cancelOrderByManager(UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(OrderStatus.ORDER_CANCELED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void rejectOrder(UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(OrderStatus.ORDER_REJECTED);
        //환불 이벤트 발행
    }

    @Transactional
    @Override
    public void changeOrderStatusByOwner(OrderStatusRequest request, UUID orderId, Long userId) {
        Order order = getOrderByOwnerIdWithLock(orderId, userId);
        order.changeStatus(request.orderStatus());
    }

    @Transactional
    @Override
    public void changeOrderStatusByManager(OrderStatusRequest request, UUID orderId) {
        Order order = getOrderWithLock(orderId);
        order.changeStatus(request.orderStatus());
    }
    @Override
    public Order createFromCheckout(Checkout checkout, User user) {
        OrderSnapshot snapshot = deserializeSnapshot(checkout.getOrderSnapshot());

        Store store = storeRepository.findById(snapshot.storeId())
                .orElseThrow(() -> new BusinessException(OrderErrorCode.STORE_NOT_FOUND));

        Order order = Order.from(checkout, snapshot, user, store);
        Order savedOrder = orderRepository.save(order);

        List<OrderLine> orderLines = snapshot.items().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.productId())
                            .orElseThrow(() -> new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND));

                    return OrderLine.create(
                            savedOrder,
                            product,
                            item.quantity().longValue(),
                            item.price()
                    );
                })
                .toList();

        orderLineRepository.saveAll(orderLines);
        return savedOrder;
    }

    private Order getOrderWithLock(UUID orderId) {
        return orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private Order getOrderByCustomerIdWithLock(UUID orderId, Long customerId) {
        return orderRepository.findByIdAndCustomerIdWithLock(orderId, customerId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.NOT_YOUR_ORDER));
    }

    private Order getOrderByOwnerIdWithLock(UUID orderId, Long ownerId) {
        return orderRepository.findByIdAndOwnerIdWithLock(orderId, ownerId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.NOT_YOUR_STORE_ORDER));
    }

    private void validateOrderAccess(OrderDetails details) {
        //권한 검증 - 유저는 자기 주문인지,
    }

    @Override
    public OrderReviewStatusResponse getReviewStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(NoSuchElementException::new);
        return new OrderReviewStatusResponse(order.isReviewed(), order.getReviewedAt());
    }

    private OrderSnapshot deserializeSnapshot(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            throw new BusinessException(OrderErrorCode.ORDER_SNAPSHOT_NOT_FOUND);
        }

        try {
            return objectMapper.treeToValue(jsonNode, OrderSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(OrderErrorCode.ORDER_SNAPSHOT_PARSE_ERROR);
        }
    }
}
