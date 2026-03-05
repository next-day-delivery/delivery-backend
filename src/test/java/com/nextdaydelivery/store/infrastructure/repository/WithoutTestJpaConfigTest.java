package com.nextdaydelivery.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.entity.StoreAddress;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("testStore")
@Import(StoreRepositoryImpl.class) // TestJpaConfig 제거
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WithoutTestJpaConfigTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private StoreAddress testAddress;

    @BeforeEach
    void setUp() {
        // 1. User 객체 생성
        testUser = User.builder()
                .username("test_user_01")
                .nickname("치킨레전드주인")
                .email("test@example.com")
                .password("password123")
                .role(UserRole.OWNER)
                .isPublic(true)
                .build();

        // 2. Auditing 필드 수동 주입 (TestJpaConfig가 없으므로 직접 넣어줌)
        // 필드명("createdAt", "createdBy")은 BaseAuditEntity에 정의된 이름과 같아야 합니다.
        ReflectionTestUtils.setField(testUser, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(testUser, "createdBy", "MANUAL_INSERT");
        ReflectionTestUtils.setField(testUser, "updatedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(testUser, "updatedBy", "MANUAL_INSERT");

        entityManager.persist(testUser);

        // 3. StoreAddress 객체 생성
        testAddress = StoreAddress.builder()
                .sido("서울특별시")
                .sigungu("종로구")
                .dong("묘동")
                .build();

        // StoreAddress도 Auditing 필드가 있다면 위와 동일하게 ReflectionTestUtils로 넣어줘야 합니다.
        // 여기서는 주소 엔티티엔 Auditing이 없다고 가정하거나 필요시 추가합니다.

        entityManager.persist(testAddress);
        entityManager.flush();
    }

    @Test
    @DisplayName("가게 저장 및 조회 테스트 - 수동 Auditing 주입")
    void saveAndFindStore() {
        Store store = Store.builder()
                .user(testUser)
                .storeAddress(testAddress)
                .name("광화문 치킨 레전드집")
                .detailAddress("돈화문로11길 20 1층")
                .build();

        // Store 엔티티도 Auditing 필드가 있다면 저장 전 혹은 저장 시점에 수동 주입 필요
        ReflectionTestUtils.setField(store, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(store, "createdBy", "MANUAL_INSERT");

        Store savedStore = storeRepository.save(store);

        assertThat(savedStore.getStoreId()).isNotNull();
        Optional<Store> found = storeRepository.findById(savedStore.getStoreId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("광화문 치킨 레전드집");
    }
}