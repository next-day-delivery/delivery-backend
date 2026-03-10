package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.AuthUser;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    //가게 리뷰 전체 조회
    @Override
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponse<Page<ReviewList>>> reviewGet(
        @PathVariable UUID storeId,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.onSuccess(reviewService.getReview(storeId, pageable)));
    }

    //내 리뷰 저장 ( 주문 내역 -> (주문 내역 리스트 표시) -> 리뷰 작성 클릭 -> 리뷰 작성, 별점 선택 )
    @Override
    @PostMapping("/write/{orderId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewPost(
        @AuthUser AuthUserDto authUser,
        @Valid @RequestBody ReviewCreateRequest request,
        @PathVariable UUID orderId) {
        Review savedReview = reviewService.saveReview(request, orderId, authUser);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(savedReview)));
    }

    //내가 작성한 리뷰 전체 조회 ( my 배민 -> 리뷰 관리 탭 )
    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<Page<ReviewList>>> myReviewListGet(
        @AuthUser AuthUserDto authUser,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.onSuccess(reviewService.getMyReview(authUser.userId(), pageable)));
    }


    //내 리뷰 공개 범위 전환( 전체 공개, 숨기기 )
    @Override
    @PatchMapping("/me/{reviewId}/visibility")
    public ResponseEntity<CommonResponse<ReviewStatusResponse>> ReviewStatusUpdate(
        @AuthUser AuthUserDto authUser,
        @PathVariable UUID reviewId
    ) {
        Review updatedReview = reviewService.updateMyReviewStatus(authUser, reviewId);

        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewStatusResponse.from(updatedReview)));
    }

    //내 리뷰 삭제 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @DeleteMapping("/me/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> reviewDelete(
        @AuthUser AuthUserDto authUser,
        @PathVariable UUID reviewId
    ) {
        reviewService.deleteMyReview(authUser, reviewId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    //내 리뷰 수정 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @PatchMapping("/me/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewUpdate(
        @AuthUser AuthUserDto authUser,
        @PathVariable UUID reviewId,
        @Valid @RequestBody ReviewCreateRequest request
    ) {
        Review updatedReview = reviewService.updateMyReview(authUser, reviewId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(updatedReview)));
    }
}
