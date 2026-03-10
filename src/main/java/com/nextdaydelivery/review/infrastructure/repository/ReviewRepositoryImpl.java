package com.nextdaydelivery.review.infrastructure.repository;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Page<Review> getReviewByStoreIdandReviewStatus(UUID storeId, ReviewStatus reviewStatus, Pageable pageable) {
        return reviewJpaRepository.findByStoreStoreIdAndReviewStatus(storeId, reviewStatus, pageable);
    }

    @Override
    public Page<Review> getReviewByUserId(Long userId, Pageable pageable) {
        return reviewJpaRepository.findByUserUserId(userId, pageable);
    }

    @Override
    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewJpaRepository.findById(reviewId);
    }

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }
}
