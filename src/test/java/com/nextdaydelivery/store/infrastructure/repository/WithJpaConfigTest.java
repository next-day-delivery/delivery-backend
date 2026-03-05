package com.nextdaydelivery.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("testStore")
@Import({StoreRepositoryImpl.class, TestJpaConfig.class}) // Auditing 설정 포함
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WithJpaConfigTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private StoreAddress testAddress;

    @BeforeEach
    void setUp() {
        // 1. User 생성 (Auditing 필드 제외 필수값만 입력)
        testUser = User.builder()
                .username("test_user_01")
                .nickname("치킨집 사장 닉네임")
                .email("test@example.com")
                .password("password123")
                .role(UserRole.OWNER)
                .isPublic(true)
                .build();

        entityManager.persist(testUser);

        // 2. StoreAddress 생성
        testAddress = StoreAddress.builder()
                .sido("서울특별시")
                .sigungu("종로구")
                .dong("묘동")
                .build();

        entityManager.persist(testAddress);

        // persist 시점에 TestJpaConfig가 createdAt, createdBy를 자동으로 채워줍니다.
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("가게 저장 및 조회 테스트 - JPA Auditing 자동 적용")
    void saveAndFindStore() {
        // Given
        // flush/clear 후에는 영속성 컨텍스트에서 다시 불러와야 관계 맵핑이 정확합니다.
        User user = entityManager.find(User.class, testUser.getUserId());
        StoreAddress address = entityManager.find(StoreAddress.class, testAddress.getStoreAddressId());

        Store store = Store.builder()
                .user(user)
                .storeAddress(address)
                .name("가게 1")
                .detailAddress("상세 주소 1")
                .build();

        // When
        Store savedStore = storeRepository.save(store);
        entityManager.flush(); // 실제 DB에 반영 시점에 Auditing 동작

        // Then
        assertThat(savedStore.getStoreId()).isNotNull();

        Optional<Store> found = storeRepository.findById(savedStore.getStoreId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("가게 1");

        // Auditing이 잘 작동했는지 추가 검증 가능
        assertThat(found.get().getCreatedBy()).isEqualTo("TEST_AUDITOR");
    }

    @Test
    @DisplayName("가게 전체 목록 조회 테스트")
    void findAllStores() {
        // Given
        User user = entityManager.find(User.class, testUser.getUserId());
        StoreAddress address = entityManager.find(StoreAddress.class, testAddress.getStoreAddressId());

        storeRepository.save(Store.builder()
                .user(user).storeAddress(address).name("가게 1").detailAddress("상세 주소 1").build());
        storeRepository.save(Store.builder()
                .user(user).storeAddress(address).name("가게 2").detailAddress("상세 주소 2").build());

        entityManager.flush();
        entityManager.clear();

        // When
        List<Store> allStores = storeRepository.findAll();

        // Then
        assertThat(allStores).hasSize(2);
        assertThat(allStores).extracting("name").containsExactlyInAnyOrder("가게 1", "가게 2");
    }

    @Test
    @DisplayName("가게 ID로 삭제 테스트")
    void deleteStoreById() {
        // Given
        User user = entityManager.find(User.class, testUser.getUserId());
        StoreAddress address = entityManager.find(StoreAddress.class, testAddress.getStoreAddressId());

        Store savedStore = storeRepository.save(Store.builder()
                .user(user).storeAddress(address).name("삭제용 가게").detailAddress("주소").build());
        UUID storeId = savedStore.getStoreId();

        entityManager.flush();
        entityManager.clear();

        // When
        storeRepository.deleteById(storeId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Store> found = storeRepository.findById(storeId);
        assertThat(found).isEmpty();
    }
}