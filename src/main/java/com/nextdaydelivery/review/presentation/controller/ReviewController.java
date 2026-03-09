package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.review.presentation.dto.response.ReviewSaveResponse;
import com.nextdaydelivery.review.presentation.dto.response.ReviewStatusResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

//RestDocs 설명 작성 부분
public interface ReviewController {

    //1. 가게 리뷰 전체 조회
    ResponseEntity<CommonResponse<List<ReviewList>>> reviewGet(
        @PathVariable UUID storeId
    );

    //2. 내 리뷰 저장
    ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewPost(
        ReviewCreateRequest review,
        @PathVariable UUID orderId
    );

    //3. 내가 작성한 리뷰 전체 조회
    ResponseEntity<CommonResponse<List<ReviewList>>> myReviewListGet(Long userId);

    //4. 리뷰 상태 ( 전체 공개, 숨기기 ) 전환
    ResponseEntity<CommonResponse<ReviewStatusResponse>> ReviewStatusUpdate(UUID reviewId);

    ResponseEntity<CommonResponse<Void>> reviewDelete(UUID reviewId);

    ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewUpdate(UUID reviewId, ReviewCreateRequest request);

}
