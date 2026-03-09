package com.nextdaydelivery.review.presentation.dto.response;

import com.nextdaydelivery.review.domain.entity.Review;

public record ReviewSaveResponse(
    String content,
    int rating,
    String message
) {
    public static ReviewSaveResponse from(Review review) {
        return new ReviewSaveResponse(
            review.getContent(),
            review.getRating(),
            "리뷰가 저장되었습니다."
        );
    }
}
