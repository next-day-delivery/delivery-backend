package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.user.domain.entity.User;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewList> getReview(UUID storeId);

    Review saveReview(
        ReviewCreateRequest request,
        UUID orderId,
        User user
    );

    List<ReviewList> getMyReview(Long userId);

    Review updateMyReviewStatus(UUID reviewId);

    Review updateMyReview(UUID reviewId, ReviewCreateRequest request);

    void deleteMyReview(UUID reviewId);
}
