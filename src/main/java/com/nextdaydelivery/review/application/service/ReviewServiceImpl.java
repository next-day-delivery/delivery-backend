package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
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
    private final StoreRepository storeRepository;

    @Override
    public List<ReviewList> getReview(UUID storeId) {
        return reviewRepository.findByStoreStoreId(storeId).stream()
            .map(ReviewList::from)
            .toList();
    }

    @Override
    public void saveReview(ReviewCreateRequest request, UUID storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(NoSuchElementException::new);
        Review review = Review.builder()
            .content(request.content())
            .rating(request.rating())
            .reviewStatus(ReviewStatus.VISIBLE)
            .store(store)
            .build();
        reviewRepository.save(review);
    }

    @Override
    public List<ReviewList> getMyReview(Long userId) {
        return reviewRepository.findByUserUserId(userId).stream()
            .map(ReviewList::from)
            .toList();
    }

    @Override
    @Transactional
    public void updateMyReviewStatus(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다. reviewId: " + reviewId));
        review.toggleStatus();
    }
}
