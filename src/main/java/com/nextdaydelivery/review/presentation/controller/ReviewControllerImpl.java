package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.review.presentation.dto.response.ReviewSaveResponse;
import com.nextdaydelivery.review.presentation.dto.response.ReviewStatusResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    //가게 리뷰 전체 조회 (인증 불필요)
    @Override
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponse<Page<ReviewList>>> reviewGet(
        @PathVariable UUID storeId,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.onSuccess(reviewService.getReview(storeId, pageable)));
    }

    //내 리뷰 저장
    @Override
    @RequireCustomerRole
    @PostMapping("/write/{orderId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewPost(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @Valid @RequestBody ReviewCreateRequest request,
        @PathVariable UUID orderId) {
        AuthUserDto authUser = principalDetails.getAuthUserDto();
        Review savedReview = reviewService.saveReview(request, orderId, authUser);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(savedReview)));
    }

    //내가 작성한 리뷰 전체 조회
    @Override
    @RequireCustomerRole
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<Page<ReviewList>>> myReviewListGet(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AuthUserDto authUser = principalDetails.getAuthUserDto();
        return ResponseEntity.ok(CommonResponse.onSuccess(reviewService.getMyReview(authUser.userId(), pageable)));
    }

    //내 리뷰 공개 범위 전환
    @Override
    @RequireCustomerRole
    @PatchMapping("/me/{reviewId}/visibility")
    public ResponseEntity<CommonResponse<ReviewStatusResponse>> ReviewStatusUpdate(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable UUID reviewId
    ) {
        AuthUserDto authUser = principalDetails.getAuthUserDto();
        Review updatedReview = reviewService.updateMyReviewStatus(authUser, reviewId);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewStatusResponse.from(updatedReview)));
    }

    //내 리뷰 삭제
    @Override
    @RequireCustomerRole
    @PatchMapping("/me/{reviewId}/delete")
    public ResponseEntity<CommonResponse<Void>> reviewDelete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable UUID reviewId
    ) {
        AuthUserDto authUser = principalDetails.getAuthUserDto();
        reviewService.deleteMyReview(authUser, reviewId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    //내 리뷰 수정
    @Override
    @RequireCustomerRole
    @PatchMapping("/me/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewUpdate(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable UUID reviewId,
        @Valid @RequestBody ReviewCreateRequest request
    ) {
        AuthUserDto authUser = principalDetails.getAuthUserDto();
        Review updatedReview = reviewService.updateMyReview(authUser, reviewId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(updatedReview)));
    }
}
