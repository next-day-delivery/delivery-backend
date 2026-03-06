package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.user.domain.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Override
    public List<ReviewList> getReview(UUID storeId) {
        return reviewRepository.findByStoreStoreId(storeId).stream()
            .map(ReviewList::from)
            .toList();
    }

    @Override
    @Transactional
    public Review saveReview(ReviewCreateRequest request, UUID orderId, User user) {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        Store store = order.getStore();

        Review review = Review.builder()
            .content(request.content())
            .rating(request.rating())
            .reviewStatus(ReviewStatus.VISIBLE)
            .user(user)
            .order(order)
            .store(store)
            .build();

        //TODO : 주문 테이블에 리뷰 완료 표시 해주는 기능 추가해야함

        return reviewRepository.save(review);
    }

    @Override
    public List<ReviewList> getMyReview(Long userId) {
        return reviewRepository.findByUserUserId(userId).stream()
            .map(ReviewList::from)
            .toList();
    }

    @Override
    @Transactional
    public Review updateMyReviewStatus(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));
        review.toggleStatus();
        return review;
    }

    @Override
    @Transactional
    public Review updateMyReview(UUID reviewId, ReviewCreateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NoSuchElementException::new);
        review.updateReview(request.content(), request.rating());
        return review;
    }

    @Override
    public void deleteMyReview(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
