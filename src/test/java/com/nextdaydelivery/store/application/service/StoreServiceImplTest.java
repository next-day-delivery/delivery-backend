package com.nextdaydelivery.store.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.request.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.request.StoreUpdateRequest;
import com.nextdaydelivery.store.presentation.dto.response.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreResponse;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    private EntityManager em;

    @InjectMocks
    private StoreServiceImpl storeService;

    // 공통으로 사용할 Mock 객체 생성 메서드들
    private User createMockUser() {
        return User.builder()
            .username("test_owner")
            .nickname("임시사장님")
            .build();
    }

    private StoreAddress createMockAddress() {
        return StoreAddress.builder()
            .sido("서울특별시")
            .sigungu("강남구")
            .dong("역삼동")
            .build();
    }

    @Test
    @DisplayName("가게 생성 - 성공")
    void createStore_success() {
        // Given
        StoreCreationRequest request = new StoreCreationRequest(
            "치킨나라", "강남구", "서울특별시", "역삼동", "테헤란로 123", List.of("치킨")
        );

        // Mocking 로직 생략 (이전과 동일)
        TypedQuery<User> mockQuery = mock(TypedQuery.class);
        given(em.createQuery(anyString(), eq(User.class))).willReturn(mockQuery);
        given(mockQuery.setParameter(anyString(), any())).willReturn(mockQuery);
        given(mockQuery.getResultList()).willReturn(List.of(createMockUser()));
        given(storeAddressService.getOrCreateAddress(any(), any(), any())).willReturn(createMockAddress());
        given(categoryService.getOrCreateCategory("치킨")).willReturn(
            Category.builder().categoryId(UUID.randomUUID()).categoryName("치킨").build());

        // When
        StoreCreationResponse response = storeService.createStore(request);

        // [로그 출력]
        System.out.println("\n✅ [가게 생성 테스트 응답 데이터]");
        System.out.println("생성된 가게명: " + response.name());
        System.out.println("카테고리 ID 리스트: " + response.categoryIds());

        // Then
        assertThat(response.name()).isEqualTo("치킨나라");
    }

    @Test
    @DisplayName("가게 상세 조회 - 성공")
    void getStore_success() {
        // Given
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder()
            .user(createMockUser())
            .storeAddress(createMockAddress())
            .name("치킨나라")
            .detailAddress("123번지")
            .build();

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeCategoryRepository.findAllByStore(store)).willReturn(List.of());

        // When
        StoreResponse response = storeService.getStore(storeId);

        // [로그 출력]
        System.out.println("\n✅ [가게 상세 조회 테스트 응답 데이터]");
        System.out.println("가게 ID: " + response.storeId());
        System.out.println("사장님 닉네임: " + response.ownerNickname());
        System.out.println("전체 주소: " + response.fullAddress());
        System.out.println("평점/리뷰: " + response.ratingAvg() + " / " + response.reviewCount());

        // Then
        assertThat(response.name()).isEqualTo("치킨나라");
    }

    @Test
    @DisplayName("가게 목록 조회 - 성공")
    void getStoreList_success() {
        // Given
        StoreSearchCondition condition = new StoreSearchCondition("치킨", null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Store mockStore = Store.builder().user(createMockUser()).storeAddress(createMockAddress()).name("치킨나라")
            .detailAddress("역삼동").build();
        Page<Store> storePage = new PageImpl<>(List.of(mockStore), pageable, 1);
        StoreCategory storeCategory = StoreCategory.builder().category(Category.builder().categoryName("치킨").build())
            .build();

        given(storeRepository.searchStores(any(), any())).willReturn(storePage);
        given(storeCategoryRepository.findFirstByStore(any())).willReturn(Optional.of(storeCategory));

        // When
        Page<StoreListResponse> result = storeService.getStoreList(condition, pageable);

        // [로그 출력]
        System.out.println("\n✅ [가게 목록 조회 테스트 데이터]");
        System.out.println("검색 결과 수: " + result.getTotalElements());
        result.getContent().forEach(s ->
            System.out.println("조회된 가게: " + s.name() + " (" + s.mainCategory() + ")")
        );

        // Then
        assertThat(result.getContent().getFirst().name()).isEqualTo("치킨나라");
    }

    @Test
    @DisplayName("가게 정보 수정 - 성공")
    void updateStore_success() {
        // 1. Given
        UUID storeId = UUID.randomUUID();
        StoreUpdateRequest request = new StoreUpdateRequest(
            "맛있어진 치킨집", "서울특별시", "강남구", "역삼동", "테헤란로 999", List.of()
        );

        // 수정 전 기존 엔티티
        Store existingStore = Store.builder()
            .user(createMockUser())
            .storeAddress(createMockAddress()) // 서울특별시 강남구 역삼동
            .name("옛날 치킨집")
            .detailAddress("테헤란로 123")
            .build();

        // 수정될 새로운 주소 객체
        StoreAddress newAddress = StoreAddress.builder()
            .sido("서울특별시").sigungu("강남구").dong("역삼동").build();

        given(storeRepository.findById(storeId)).willReturn(Optional.of(existingStore));
        given(storeAddressService.getOrCreateAddress(anyString(), anyString(), anyString())).willReturn(newAddress);
        given(storeCategoryRepository.findAllByStore(any())).willReturn(List.of());

        // [수정 전 로그]
        System.out.println("\n✅ [가게 수정 테스트 - 데이터 변화 확인]");
        System.out.println("수정 전 이름: " + existingStore.getName());
        System.out.println("수정 전 상세주소: " + existingStore.getDetailAddress());

        // 2. When
        StoreResponse response = storeService.updateStore(storeId, request);

        // 3. Then
        // [수정 후 로그]
        System.out.println("수정 후 이름: " + response.name());
        System.out.println("수정 후 상세주소: " + response.fullAddress()); // fullAddress는 조합된 문자열

        assertThat(response.name()).isEqualTo("맛있어진 치킨집");
        verify(storeCategoryService).updateStoreCategories(eq(existingStore), any());
    }

    @Test
    @DisplayName("가게 삭제 - 성공 (Soft Delete 검증)")
    void deleteStore_success() {
        // Given
        UUID storeId = UUID.randomUUID();
        Store store = Store.builder().user(createMockUser()).storeAddress(createMockAddress()).name("삭제될가게")
            .detailAddress("주소").build();
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // [삭제 전 로그]
        System.out.println("\n✅ [가게 삭제 테스트 - 상태 변화 확인]");
        System.out.println("삭제 전: deletedAt=" + store.getDeletedAt() + ", deletedBy=" + store.getDeletedBy());

        // When
        storeService.deleteStore(storeId, "admin_user");

        // [삭제 후 로그]
        System.out.println("삭제 후: deletedAt=" + store.getDeletedAt() + ", deletedBy=" + store.getDeletedBy());

        // Then
        assertThat(store.getDeletedAt()).isNotNull();
        assertThat(store.getDeletedBy()).isEqualTo("admin_user");
    }
}
