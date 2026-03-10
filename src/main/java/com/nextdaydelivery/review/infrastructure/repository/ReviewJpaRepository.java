package com.nextdaydelivery.review.infrastructure.repository;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByUserUserId(Long userId, Pageable pageable);

    Page<Review> findByStoreStoreIdAndReviewStatus(UUID storeId, ReviewStatus reviewStatus, Pageable pageable);
}
