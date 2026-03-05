package com.nextdaydelivery.review.presentation.dto.response;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import java.util.UUID;

public record ReviewList(
        UUID reviewId,
        String content,
        Integer rating,
        ReviewStatus reviewStatus
) {
    public static ReviewList from(Review review) {
        return new ReviewList(
                review.getReviewId(),
                review.getContent(),
                review.getRating(),
                review.getReviewStatus()
        );
    }
}
