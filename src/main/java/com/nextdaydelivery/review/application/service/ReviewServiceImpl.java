package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final StoreReviewService storeReviewService;
    private final UserRepository userRepository;

    @Override
    public Page<ReviewList> getReview(UUID storeId, Pageable pageable) {
        return reviewRepository.findByStoreStoreIdAndReviewStatus(storeId, ReviewStatus.VISIBLE, pageable)
            .map(ReviewList::from);
    }

    @Override
    @Transactional
    public Review saveReview(ReviewCreateRequest request, UUID orderId, AuthUserDto authUser) {
        User user = userRepository.findById(authUser.userId()).orElseThrow(NoSuchElementException::new);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(NoSuchElementException::new);
        Store store = order.getStore();
        Review review = Review.create(request, user, order, store);

        order.markAsReviewed();
        storeReviewService.plusReviewSummary(store, request.rating());
        return reviewRepository.save(review);
    }

    @Override
    public Page<ReviewList> getMyReview(Long userId, Pageable pageable) {
        return reviewRepository.findByUserUserId(userId, pageable)
            .map(ReviewList::from);
    }

    @Override
    @Transactional
    public Review updateMyReviewStatus(AuthUserDto authUser, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getUserId().equals(authUser.userId())) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN); // 403
        }

        review.toggleStatus();
        return review;
    }

    @Override
    @Transactional
    public Review updateMyReview(AuthUserDto authUser, UUID reviewId, ReviewCreateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NoSuchElementException::new);
        if (!review.getUser().getUserId().equals(authUser.userId())) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN); // 403
        }

        review.updateReview(request.content(), request.rating());
        return review;
    }

    @Override
    @Transactional
    public void deleteMyReview(AuthUserDto authUser, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NoSuchElementException::new);
        if (!review.getUser().getUserId().equals(authUser.userId())) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN); // 403
        }

        storeReviewService.minusReviewSummary(review.getStore(), review.getRating());
        review.delete(authUser.userId().toString());
    }
}
