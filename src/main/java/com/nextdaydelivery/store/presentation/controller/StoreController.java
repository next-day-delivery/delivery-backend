package com.nextdaydelivery.store.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.StoreResponse;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.StoreUpdateRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreReviewService storeReviewService;

    @PostMapping
    public CommonResponse<StoreCreationResponse> createStore(@Valid @RequestBody StoreCreationRequest request) {

        StoreCreationResponse response = storeService.createStore(request);
        return CommonResponse.onSuccess(response);
    }

    // 2. 가게 상세 조회
    @GetMapping("/{storeId}")
    public CommonResponse<StoreResponse> getStore(@PathVariable UUID storeId) {
        StoreResponse response = storeService.getStore(storeId);
        return CommonResponse.onSuccess(response);
    }

    // 3. 가게 정보 수정
    @PatchMapping("/{storeId}")
    public CommonResponse<StoreResponse> updateStore(
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreUpdateRequest request) {
        StoreResponse response = storeService.updateStore(storeId, request);
        return CommonResponse.onSuccess(response);
    }

    // 4. 가게 삭제 (Soft Delete)
    @DeleteMapping("/{storeId}")
    public CommonResponse<String> deleteStore(
        @PathVariable UUID storeId,
        @RequestParam String deletedBy) { // 실제 서비스에선 인증 객체(User)에서 추출 권장
        storeService.deleteStore(storeId, deletedBy);
        return CommonResponse.onSuccess("가게가 성공적으로 삭제되었습니다.");
    }

    // 5. 가게 목록 조회 (검색 및 페이징)
    // 예시 URL: /api/stores?name=치킨&page=0&size=10&sort=createdAt,desc
    @GetMapping
    public CommonResponse<Page<StoreListResponse>> getStoreList(
        StoreSearchCondition condition,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StoreListResponse> response = storeService.getStoreList(condition, pageable);
        return CommonResponse.onSuccess(response);
    }

    // 6. 가게 평점 조회
    @GetMapping("/{storeId}/rating")
    public void getStoreRatingAvg(
        @PathVariable UUID storeId
    ) {
        double storeRating = storeReviewService.getStoreRatingAvg(storeId);
    }

    // 7. 리뷰 수 조회
    @GetMapping("/{storeId}/reviewCount")
    public void getStoreReviewCount(
        @PathVariable UUID storeId
    ) {
        int reviewCount = storeReviewService.getStoreReviewCount(storeId);
    }
}
