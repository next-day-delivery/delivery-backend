package com.nextdaydelivery.store.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextdaydelivery.store.presentation.dto.StoreResponse;
import com.nextdaydelivery.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreDomainTest {

    @Test
    @DisplayName("가게 수정 로직 검증 - 엔티티 상태가 변경되어야 한다")
    void store_update_logic() {
        // 1. Given
        Store store = createBaseStore("원래 이름", "원래 상세주소");
        StoreAddress newAddress = StoreAddress.builder()
                .sido("경기도").sigungu("성남시").dong("판교동").build();

        // 2. When
        store.update("바뀐 이름", "바뀐 상세주소", newAddress);

        // 3. Then
        assertThat(store.getName()).isEqualTo("바뀐 이름");
        assertThat(store.getDetailAddress()).isEqualTo("바뀐 상세주소");
        assertThat(store.getStoreAddress()).isEqualTo(newAddress);
    }

    @Test
    @DisplayName("Soft Delete 로직 검증 - 삭제 시간과 삭제자가 기록되어야 한다")
    void store_delete_logic() {
        // 1. Given
        Store store = createBaseStore("가게", "주소");
        String deletedBy = "admin_user";

        // 2. When
        store.delete(deletedBy);

        // 3. Then
        assertThat(store.getDeletedAt()).isNotNull();
        assertThat(store.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(store.getDeletedBy()).isEqualTo(deletedBy);
    }

    @Test
    @DisplayName("DTO 변환 로직 검증 - fullAddress가 올바른 포맷으로 조합되어야 한다")
    void store_response_mapping_logic() {
        // 1. Given
        StoreAddress address = StoreAddress.builder()
                .sido("서울특별시").sigungu("강남구").dong("역삼동").build();
        Store store = Store.builder()
                .user(User.builder().nickname("사장님").build())
                .storeAddress(address)
                .name("치킨집")
                .detailAddress("테헤란로 123")
                .build();

        List<String> categories = List.of("치킨", "야식");

        // 2. When (StoreResponse.from 정적 팩토리 메서드 테스트)
        StoreResponse response = StoreResponse.from(store, "사장님", categories);

        // 3. Then
        String expectedAddress = "서울특별시 강남구 역삼동 테헤란로 123";
        assertThat(response.fullAddress()).isEqualTo(expectedAddress);
        System.out.println("✅ 조합된 주소 로그: " + response.fullAddress());
    }

    // 테스트용 헬퍼 메서드
    private Store createBaseStore(String name, String detailAddress) {
        return Store.builder()
                .user(User.builder().build())
                .storeAddress(StoreAddress.builder().build())
                .name(name)
                .detailAddress(detailAddress)
                .build();
    }
}