package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewList> getReview(UUID storeId);

    void saveReview(
            ReviewCreateRequest request,
            UUID storeId
    );

    List<ReviewList> getMyReview(Long userId);

    void updateMyReviewStatus(UUID reviewId);
}
