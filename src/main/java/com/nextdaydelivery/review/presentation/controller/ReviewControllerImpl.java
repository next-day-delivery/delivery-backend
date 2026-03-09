package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.review.presentation.dto.response.ReviewSaveResponse;
import com.nextdaydelivery.review.presentation.dto.response.ReviewStatusResponse;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    //가게 리뷰 전체 조회
    @Override
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponse<List<ReviewList>>> reviewGet(
        @PathVariable UUID storeId
    ) {
        List<ReviewList> reviews = reviewService.getReview(storeId);
        return ResponseEntity.ok(CommonResponse.onSuccess(reviews));
    }

    //내 리뷰 저장 ( 주문 내역 -> (주문 내역 리스트 표시) -> 리뷰 작성 클릭 -> 리뷰 작성, 별점 선택 )
    @Override
    @PostMapping("/write/{orderId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewPost(
        @Valid @RequestBody ReviewCreateRequest request,
        @PathVariable UUID orderId) {
        User user = User.builder().build(); //임시 유저 //TODO 추후 컨텍스트에서 User 객체 꺼내서 사용
        Review savedReview = reviewService.saveReview(request, orderId, user); // TODO 반환값 설정
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(savedReview)));
    }

    //내가 작성한 리뷰 전체 조회 ( my 배민 -> 리뷰 관리 탭 )
    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<List<ReviewList>>> myReviewListGet(
        @RequestParam Long userId
        //TODO 추후 컨텍스트에서 User 객체 꺼내서 사용
    ) {
        List<ReviewList> reviews = reviewService.getMyReview(userId);
        return ResponseEntity.ok(CommonResponse.onSuccess(reviews));
    }


    //내 리뷰 공개 범위 전환( 전체 공개, 숨기기 )
    @Override
    @PatchMapping("/me/{reviewId}/visibility")
    public ResponseEntity<CommonResponse<ReviewStatusResponse>> ReviewStatusUpdate(
        @PathVariable UUID reviewId
    ) {
        Review updatedReview = reviewService.updateMyReviewStatus(reviewId);

        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewStatusResponse.from(updatedReview)));
    }

    //내 리뷰 삭제 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @DeleteMapping("/me/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> reviewDelete(
        @PathVariable UUID reviewId
    ) {
        reviewService.deleteMyReview(reviewId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    //내 리뷰 수정 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @PatchMapping("/me/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewUpdate(
        @PathVariable UUID reviewId,
        @Valid @RequestBody ReviewCreateRequest request
    ) {
        Review updatedReview = reviewService.updateMyReview(reviewId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(ReviewSaveResponse.from(updatedReview)));
    }
}
