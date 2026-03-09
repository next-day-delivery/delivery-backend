package com.nextdaydelivery.store.presentation.dto;

public record StoreSearchCondition(
        String name,       // 가게 이름 검색
        String category,   // 카테고리 필터
        String sido,       // 지역 필터 (시/도)
        String sigungu,    // 지역 필터 (시/군/구)
        Double minRating   // 최소 평점 필터
) {
}
