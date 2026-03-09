package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
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

    // DIP, 의존성, 클린 아키텍쳐
    // JPA 영속성 컨텍스트와 트랜잭션 외부에서 Entity를 접근하는 경우 발생하는 문제
    // 서비스 계층의 역할과 책임
    // TODO : 튜터님 조언 추후 반영 예정
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

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
        Review review = Review.create(request, user, order, store);

        //TODO : 주문 테이블에 리뷰 완료 표시 해주는 기능 추가해야함
        //TODO : 현재는 orderId만으로 리뷰를 생성하므로
        // 본인 주문인지, 리뷰 작성 가능한 상태인지 보장되지 않는 상태.
        // 저장 전에 소유자 일치·완료 상태·중복 리뷰 여부를 검증필요

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
