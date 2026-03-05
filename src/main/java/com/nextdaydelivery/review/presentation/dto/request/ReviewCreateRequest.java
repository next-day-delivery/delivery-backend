package com.nextdaydelivery.review.presentation.dto.request;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.store.domain.entity.Store;

public record ReviewCreateRequest(
        String content, // 리뷰 내용
        Integer rating // 별점 (INT)
) {
    public Review toEntity(Store store) {
        return Review.builder()
                .content(content)
                .rating(rating)
                .reviewStatus(ReviewStatus.VISIBLE)
                .store(store)
                .build();
    }
}
