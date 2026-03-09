package com.nextdaydelivery.store.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.nextdaydelivery.store.domain.entity.Category;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.entity.StoreCategory;
import com.nextdaydelivery.store.domain.repository.StoreCategoryRepository;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.store.domain.service.CategoryService;
import com.nextdaydelivery.store.domain.service.StoreAddressService;
import com.nextdaydelivery.store.domain.service.StoreCategoryService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.StoreResponse;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreCategoryRepository storeCategoryRepository;
    @Mock
    private StoreAddressService storeAddressService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private StoreCategoryService storeCategoryService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    // --- Helper Methods ---
    private User createMockUser() {
        User user = User.builder()
                .username("test_user")
                .nickname("테스트사장님")
                .email("test@test.com")
                .password("password")
                .build();
        // 빌더에 없는 userId를 리플렉션으로 주입
        ReflectionTestUtils.setField(user, "userId", 1L);
        return user;
    }

    private StoreAddress createMockAddress() {
        return StoreAddress.builder()
                .sido("서울").sigungu("강남구").dong("역삼동")
                .build();
    }

    private Store createMockStore(User user, StoreAddress address) {
        Store store = Store.builder()
                .name("조회맛집")
                .user(user)
                .storeAddress(address)
                .detailAddress("123번지")
                .build();

        // 빌더에 없는 필드들을 리플렉션으로 주입
        ReflectionTestUtils.setField(store, "storeId", UUID.randomUUID());
        ReflectionTestUtils.setField(store, "ratingAvg", new BigDecimal("4.5"));
        ReflectionTestUtils.setField(store, "reviewCount", 10);
        return store;
    }

    // --- 1. 가게 생성 테스트 ---
    @Test
    @DisplayName("가게 생성 - 성공")
    void createStore_success() {
        Long userId = 1L;
        StoreCreationRequest request = new StoreCreationRequest(
                "넥스트치킨", "강남구", "서울", "역삼동", "상세주소", List.of("치킨")
        );
        User user = createMockUser();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(storeAddressService.getOrCreateAddress(any(), any(), any())).willReturn(createMockAddress());
        given(categoryService.getOrCreateCategory(anyString())).willReturn(
                Category.builder().categoryId(UUID.randomUUID()).categoryName("치킨").build());

        StoreCreationResponse response = storeService.createStore(request, userId);

        assertThat(response.name()).isEqualTo("넥스트치킨");
        verify(storeRepository).save(any(Store.class));
    }

    // --- 2. 가게 상세 조회 테스트 ---
    @Test
    @DisplayName("가게 상세 조회 - 성공")
    void getStore_success() {
        UUID storeId = UUID.randomUUID();
        Store store = createMockStore(createMockUser(), createMockAddress());

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeCategoryRepository.findAllByStore(store)).willReturn(List.of());

        StoreResponse response = storeService.getStore(storeId);

        assertThat(response.name()).isEqualTo("조회맛집");
        assertThat(response.ratingAvg()).isEqualByComparingTo("4.5");
    }

    // --- 3. 가게 목록 조회 테스트 ---
    @Test
    @DisplayName("가게 목록 조회 - 성공")
    void getStoreList_success() {
        StoreSearchCondition condition = new StoreSearchCondition("맛집", null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Store store = createMockStore(createMockUser(), createMockAddress());
        Page<Store> page = new PageImpl<>(List.of(store), pageable, 1);

        Category category = Category.builder().categoryName("치킨").build();
        StoreCategory sc = StoreCategory.builder().store(store).category(category).build();

        given(storeRepository.searchStores(any(), any())).willReturn(page);
        given(storeCategoryRepository.findAllByStoreIn(any())).willReturn(List.of(sc));

        Page<StoreListResponse> result = storeService.getStoreList(condition, pageable);

        assertThat(result.getContent().get(0).mainCategory()).isEqualTo("치킨");
    }

    @Test
    @DisplayName("가게 삭제 - 성공")
    void deleteStore_success() {
        UUID storeId = UUID.randomUUID();
        Store store = createMockStore(createMockUser(), createMockAddress());
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        storeService.deleteStore(storeId, "admin");

        // Soft Delete 확인 (엔티티 내부 필드 체크)
        assertThat(ReflectionTestUtils.getField(store, "deletedAt")).isNotNull();
    }
}
