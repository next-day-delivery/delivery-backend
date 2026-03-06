package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    //가게 리뷰 전체 조회
    @Override
    @GetMapping("/{storeId}")
    public ResponseEntity<List<ReviewList>> reviewGet(
        @PathVariable UUID storeId
    ) {
        List<ReviewList> reviews = reviewService.getReview(storeId);
        return ResponseEntity.ok(reviews);
    }

    //내 리뷰 저장 ( 주문 내역 -> (주문 내역 리스트 표시) -> 리뷰 작성 클릭 -> 리뷰 작성, 별점 선택 )
    @Override
    @PostMapping("/write/{orderId}")
    public ResponseEntity<Response> reviewPost(
        @Valid @RequestBody ReviewCreateRequest request,
        @PathVariable UUID orderId) {
        User user = User.builder().build(); //임시 유저
        reviewService.saveReview(request, orderId, user); // TODO 반환값 설정
        return ResponseEntity.ok().build();
    }

    //내가 작성한 리뷰 전체 조회 ( my 배민 -> 리뷰 관리 탭 )
    @Override
    @GetMapping("/me")
    public ResponseEntity<List<ReviewList>> myReviewListGet(
        @RequestParam Long userId
    ) {
        List<ReviewList> reviews = reviewService.getMyReview(userId);
        return ResponseEntity.ok(reviews);
    }


    //내 리뷰 공개 범위 전환( 전체 공개, 숨기기 )
    @Override
    @PatchMapping("/me/{reviewId}/visibility")
    public void ReviewStatusUpdate(
        @PathVariable UUID reviewId
    ) {
        reviewService.updateMyReviewStatus(reviewId);
    }

    //내 리뷰 삭제 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @DeleteMapping("/me/{reviewId}")
    public void reviewDelete(
        @PathVariable UUID reviewId
    ) {
        reviewService.deleteMyReview(reviewId);
    }

    //내 리뷰 수정 (내 리뷰가 보이는 모든 곳에 수정,삭제 버튼 있음)
    @Override
    @PatchMapping("/me/{reviewId}")
    public void reviewUpdate(
        @PathVariable UUID reviewId,
        @RequestBody ReviewCreateRequest request
    ) {
        reviewService.updateMyReview(reviewId, request);
    }
}
