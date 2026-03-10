package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.domain.service.CategoryService;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.request.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.request.StoreUpdateRequest;
import com.nextdaydelivery.store.presentation.dto.response.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreResponse;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
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
    private final EntityManager em; // 임시 테스트를 위해 주입

    @Transactional
    @Override
    public StoreCreationResponse createStore(StoreCreationRequest request) {
        // 가게 주소를 가게 주소 테이블에 저장하기, 이때 중복 체크 해야함
        StoreAddress storeAddress = storeAddressService.getOrCreateAddress(
            request.sigungu(),
            request.sido(),
            request.dong()
        );

        // 2. [수정] 임시 유저 조회 또는 생성
        String testUsername = "test_owner";
        User dummyUser;

        // JPQL을 사용하여 기존 유저가 있는지 확인
        List<User> existingUsers = em.createQuery("select u from User u where u.username = :username", User.class)
            .setParameter("username", testUsername)
            .getResultList();

        if (existingUsers.isEmpty()) {
            // 없으면 새로 생성 후 저장
            dummyUser = User.builder()
                .username(testUsername)
                .nickname("임시사장님")
                .email("test@test.com")
                .password("1234")
                .role(UserRole.OWNER)
                .isPublic(true)
                .build();
            em.persist(dummyUser);
        } else {
            // 있으면 기존 유저 사용
            dummyUser = existingUsers.get(0);
        }

//        // 가게에 기본정보 저장
//        Store store = storeRepository.save(request.toEntity(storeAddress));

        // 3. 가게 저장 (빌더에 .user(dummyUser) 추가 필요!)
        Store store = Store.builder()
            .user(dummyUser) // 핵심: 여기서 유저를 넣어줘야 에러가 안 납니다.
            .storeAddress(storeAddress)
            .name(request.name())
            .detailAddress(request.detailAddress())
            .build();

        storeRepository.save(store);

        // 4. [개선됨] 카테고리 처리: 정규화(trim) 후 중복 제거(Set)
        List<UUID> savedCategoryIds = new ArrayList<>();

        if (request.categoryNames() != null && !request.categoryNames().isEmpty()) {
            // 먼저 공백 제거 및 중복 제거 수행
            LinkedHashSet<String> normalizedCategoryNames = new LinkedHashSet<>();
            for (String rawCategoryName : request.categoryNames()) {
                if (rawCategoryName == null || rawCategoryName.isBlank()) {
                    throw new IllegalArgumentException("categoryNames에는 빈 값을 포함할 수 없습니다.");
                }
                normalizedCategoryNames.add(rawCategoryName.trim());
            }

            // 정제된 이름들에 대해서만 로직 수행
            for (String categoryName : normalizedCategoryNames) {
                Category category = categoryService.getOrCreateCategory(categoryName);
                storeCategoryService.createStoreCategory(store, category);
                savedCategoryIds.add(category.getCategoryId());
            }
        }

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
        Page<Store> storePage = storeRepository.searchStores(condition, pageable);

        return storePage.map(store -> {
            String mainCategory = storeCategoryRepository.findFirstByStore(store)
                .map(sc -> sc.getCategory().getCategoryName())
                .orElse("미지정");

            return StoreListResponse.from(store, mainCategory);
        });
    }

    private StoreResponse convertToStoreResponse(Store store) {
        List<String> categoryNames = storeCategoryRepository.findAllByStore(store).stream()
            .map(sc -> sc.getCategory().getCategoryName())
            .toList();

        return StoreResponse.from(store, store.getUser().getNickname(), categoryNames);
    }
}
