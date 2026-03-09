package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.domain.service.CategoryService;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.StoreResponse;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.StoreUpdateRequest;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreAddressService storeAddressService;
    private final CategoryService categoryService;         // 카테고리 조회용
    private final StoreCategoryService storeCategoryService; // 연결용
    private final UserRepository userRepository;

    @Transactional
    @Override
    public StoreCreationResponse createStore(StoreCreationRequest request, Long username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 가게 주소를 가게 주소 테이블에 저장, 중복 체크
        StoreAddress storeAddress = storeAddressService.getOrCreateAddress(
                request.sigungu(),
                request.sido(),
                request.dong()
        );
        Store store = Store.builder()
                .user(user)
                .storeAddress(storeAddress)
                .name(request.name())
                .detailAddress(request.detailAddress())
                .build();
        storeRepository.save(store);

        // 4. 카테고리 처리 (중복 제거 및 등록)
        List<UUID> savedCategoryIds = processCategories(store, request.categoryNames());
        return StoreCreationResponse.from(store, savedCategoryIds);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다. ID: " + storeId));

        return convertToStoreResponse(store);
    }

    @Transactional
    @Override
    public StoreResponse updateStore(UUID storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 가게가 존재하지 않습니다."));

        // 주소 및 카테고리 업데이트
        StoreAddress newAddress = storeAddressService.getOrCreateAddress(
                request.sigungu(), request.sido(), request.dong()
        );

        store.update(request.name(), request.detailAddress(), newAddress);

        if (request.categoryIds() != null) {
            storeCategoryService.updateStoreCategories(store, request.categoryIds());
        }

        // DTO로 변환해서 반환
        return convertToStoreResponse(store);
    }

    @Transactional
    @Override
    public void deleteStore(UUID storeId, String deletedBy) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 가게가 존재하지 않습니다."));
        store.delete(deletedBy);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StoreListResponse> getStoreList(StoreSearchCondition condition, Pageable pageable) {
        // 1. 가게 목록 조회 (Querydsl - 1번)
        Page<Store> storePage = storeRepository.searchStores(condition, pageable);
        List<Store> stores = storePage.getContent();

        if (stores.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. 카테고리 일괄 조회 (Querydsl - 1번, N+1 방지)
        List<StoreCategory> allStoreCategories = storeCategoryRepository.findAllByStoreIn(stores);

        // 3. 메모리 매핑 및 DTO 변환
        Map<UUID, List<String>> categoryMap = allStoreCategories.stream()
                .collect(Collectors.groupingBy(
                        sc -> sc.getStore().getStoreId(),
                        Collectors.mapping(sc -> sc.getCategory().getCategoryName(), Collectors.toList())
                ));

        return storePage.map(store -> {
            List<String> categories = categoryMap.getOrDefault(store.getStoreId(), List.of());
            return StoreListResponse.from(store, categories.isEmpty() ? "미지정" : categories.getFirst());
        });
    }

    private StoreResponse convertToStoreResponse(Store store) {
        List<String> categoryNames = storeCategoryRepository.findAllByStore(store).stream()
                .map(sc -> sc.getCategory().getCategoryName())
                .toList();

        return StoreResponse.from(store, store.getUser().getNickname(), categoryNames);
    }

    // 가독성을 위해 카테고리 로직 분리
    private List<UUID> processCategories(Store store, List<String> categoryNames) {
        List<UUID> savedCategoryIds = new ArrayList<>();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return savedCategoryIds;
        }

        LinkedHashSet<String> normalizedNames = new LinkedHashSet<>();
        for (String rawName : categoryNames) {
            if (rawName != null && !rawName.isBlank()) {
                normalizedNames.add(rawName.trim());
            }
        }

        for (String name : normalizedNames) {
            Category category = categoryService.getOrCreateCategory(name);
            storeCategoryService.createStoreCategory(store, category);
            savedCategoryIds.add(category.getCategoryId());
        }
        return savedCategoryIds;
    }
}
