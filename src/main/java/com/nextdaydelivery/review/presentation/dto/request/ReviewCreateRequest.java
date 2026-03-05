package com.nextdaydelivery.review.presentation.dto.request;

public record ReviewCreateRequest(
    String content, // 리뷰 내용
    Integer rating // 별점 (INT)
) {
}
