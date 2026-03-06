package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import java.util.List;
import java.util.UUID;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

//RestDocs 설명 작성 부분
public interface ReviewApi {

    //1. 가게 리뷰 전체 조회
    ResponseEntity<List<ReviewList>> reviewGet(
            @PathVariable UUID storeId
    );

    //2. 내 리뷰 저장
    ResponseEntity<Response> reviewPost(
            ReviewCreateRequest review,
            @PathVariable UUID orderId
    );

    //3. 내가 작성한 리뷰 전체 조회
    ResponseEntity<List<ReviewList>> myReviewListGet(Long userId);

    //4. 리뷰 상태 ( 전체 공개, 숨기기 ) 전환
    void ReviewStatusUpdate(UUID reviewId);

    void reviewDelete(UUID reviewId);

    void reviewUpdate(UUID reviewId, ReviewCreateRequest request);

}
