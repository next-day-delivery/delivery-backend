package com.nextdaydelivery.store.domain.service;

import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.StoreResponse;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.StoreUpdateRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    // 1. 가게 생성
    StoreCreationResponse createStore(StoreCreationRequest request, Long username);

    // 2. 단건 상세 조회
    StoreResponse getStore(UUID storeId);

    // 3. 가게 정보 수정 (StoreUpdateResponse 대신 StoreResponse 재사용)
    // 수정 후 변경된 전체 상세 데이터를 보여주는 것이 프론트엔드 작업에 유리합니다.
    StoreResponse updateStore(UUID storeId, StoreUpdateRequest request, String updatedBy);

    // 4. 가게 삭제
    void deleteStore(UUID storeId, String deletedBy);

    // 5. 목록 조회
    Page<StoreListResponse> getStoreList(StoreSearchCondition condition, Pageable pageable);
}
