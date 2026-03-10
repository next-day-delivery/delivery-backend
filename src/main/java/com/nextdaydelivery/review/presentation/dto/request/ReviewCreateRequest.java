package com.nextdaydelivery.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(
    @NotBlank String content, // 리뷰 내용 필수
    @Min(1) @Max(5) int rating // 별점 1~5 제한
) {
}
