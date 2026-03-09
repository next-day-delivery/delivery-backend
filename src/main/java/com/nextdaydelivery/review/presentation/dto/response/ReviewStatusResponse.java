package com.nextdaydelivery.review.presentation.dto.response;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;

public record ReviewStatusResponse(
    ReviewStatus reviewStatus,
    String message
) {
    public static ReviewStatusResponse from(Review review) {
        return new ReviewStatusResponse(
            review.getReviewStatus(),
            review.getReviewStatus() == ReviewStatus.VISIBLE
                ? "리뷰가 공개 상태로 전환되었습니다."
                : "리뷰가 숨김 상태로 전환되었습니다."
        );
    }
}
