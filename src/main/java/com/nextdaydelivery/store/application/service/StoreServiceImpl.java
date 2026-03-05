package com.nextdaydelivery.store.application.service;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.domain.service.CategoryService;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
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

        // 3. 카테고리 처리 (이름 기반 getOrCreateCategory 사용)
        List<UUID> savedCategoryIds = new ArrayList<>();

        if (request.categoryNames() != null && !request.categoryNames().isEmpty()) {
            for (String rawCategoryName : new LinkedHashSet<>(request.categoryNames())) {
                if (rawCategoryName == null || rawCategoryName.isBlank()) {
                    throw new IllegalArgumentException("categoryNames에는 빈 값을 포함할 수 없습니다.");
                }
                String categoryName = rawCategoryName.trim();
                // [핵심] 보내주신 서비스 메서드 사용: 이름으로 조회하거나 없으면 생성함
                Category category = categoryService.getOrCreateCategory(categoryName);

                // 중간 테이블(StoreCategory)에 저장
                storeCategoryService.createStoreCategory(store, category);

                // 응답에 담아줄 ID 수집
                savedCategoryIds.add(category.getCategoryId());
            }
        }

        // 4. 최종 응답 반환
        return StoreCreationResponse.from(store, savedCategoryIds);

    }
}
