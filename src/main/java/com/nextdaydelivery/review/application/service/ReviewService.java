package com.nextdaydelivery.review.application.service;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<ReviewList> getReview(UUID storeId, Pageable pageable);

    Review saveReview(
        ReviewCreateRequest request,
        UUID orderId,
        AuthUserDto authUser
    );

    Page<ReviewList> getMyReview(Long userId, Pageable pageable);

    Review updateMyReviewStatus(AuthUserDto authUser, UUID reviewId);

    Review updateMyReview(AuthUserDto authUser, UUID reviewId, ReviewCreateRequest request);

    void deleteMyReview(AuthUserDto authUser, UUID reviewId);
}
