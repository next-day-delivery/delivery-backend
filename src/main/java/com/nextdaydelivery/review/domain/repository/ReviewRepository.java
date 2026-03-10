package com.nextdaydelivery.review.domain.repository;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository {

    Page<Review> getReviewByStoreIdandReviewStatus(UUID storeId, ReviewStatus reviewStatus, Pageable pageable);

    Page<Review> getReviewByUserId(Long userId, Pageable pageable);

    Review save(Review review);

    Optional<Review> getReviewById(UUID reviewId);
}
