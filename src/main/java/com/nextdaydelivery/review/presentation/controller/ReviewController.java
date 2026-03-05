package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
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

    //내 리뷰 저장
    @Override
    @PostMapping("/write/{storeId}")
    public ResponseEntity<Response> reviewPost(
        @Valid @RequestBody ReviewCreateRequest request,
        @PathVariable UUID storeId) {
        reviewService.saveReview(request, storeId);
        return ResponseEntity.ok().build();
    }

    //내가 작성한 리뷰 전체 조회
    @Override
    @GetMapping("/me")
    public ResponseEntity<List<ReviewList>> myReviewListGet(
        @RequestParam Long userId
    ) {
        List<ReviewList> reviews = reviewService.getMyReview(userId);
        return ResponseEntity.ok(reviews);
    }


    //내 리뷰 상태 ( 공개, 숨기기 ) 전환
    @Override
    @PatchMapping("/me/status/{reviewId}")
    public void ratingStatusUpdate(
        @PathVariable UUID reviewId
    ) {
        reviewService.updateMyReviewStatus(reviewId);
    }
}
