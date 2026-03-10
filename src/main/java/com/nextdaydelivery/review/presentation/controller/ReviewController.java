package com.nextdaydelivery.review.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.review.presentation.dto.response.ReviewSaveResponse;
import com.nextdaydelivery.review.presentation.dto.response.ReviewStatusResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

//RestDocs 설명 작성 부분
public interface ReviewController {

    //1. 가게 리뷰 전체 조회
    ResponseEntity<CommonResponse<Page<ReviewList>>> reviewGet(
        @PathVariable UUID storeId,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    //2. 내 리뷰 저장
    ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewPost(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        ReviewCreateRequest review,
        @PathVariable UUID orderId
    );

    //3. 내가 작성한 리뷰 전체 조회
    ResponseEntity<CommonResponse<Page<ReviewList>>> myReviewListGet(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    //4. 리뷰 상태 ( 전체 공개, 숨기기 ) 전환
    ResponseEntity<CommonResponse<ReviewStatusResponse>> ReviewStatusUpdate(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        UUID reviewId
    );

    ResponseEntity<CommonResponse<Void>> reviewDelete(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        UUID reviewId
    );

    ResponseEntity<CommonResponse<ReviewSaveResponse>> reviewUpdate(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        UUID reviewId,
        ReviewCreateRequest request
    );
}
