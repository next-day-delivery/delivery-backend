package com.nextdaydelivery.store.presentation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.store.presentation.dto.request.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.request.StoreUpdateRequest;
import com.nextdaydelivery.store.presentation.dto.response.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {StoreController.class})
class StoreControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private StoreReviewService storeReviewService;

    private AuthUserDto mockUser;
    private PrincipalDetails principal;

    @BeforeEach
    void setUp() {
        // 인증된 유저 정보 세팅 (OWNER 권한)
        mockUser = new AuthUserDto(1L, UserRole.OWNER);
        principal = new PrincipalDetails(mockUser);
    }

    @Test
    @DisplayName("가게 등록 - 성공")
    void createStore() throws Exception {
        StoreCreationRequest request = new StoreCreationRequest(
                "넥스트 치킨", "강남구", "서울특별시", "역삼동", "테헤란로 123", List.of("치킨")
        );
        StoreCreationResponse response = new StoreCreationResponse(
                UUID.randomUUID(), "넥스트 치킨", "강남구", "서울특별시", "역삼동", "테헤란로 123", List.of(UUID.randomUUID())
        );

        given(storeService.createStore(any(StoreCreationRequest.class), eq(1L))).willReturn(response);

        mockMvc.perform(post("/api/stores")
                        .with(user(principal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 상세 조회 - 성공")
    void getStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreResponse response = new StoreResponse(
                storeId, "넥스트 맛집", "사장님", "서울특별시 강남구 역삼동 테헤란로 123",
                new BigDecimal("4.8"), 100, List.of("치킨")
        );

        given(storeService.getStore(storeId)).willReturn(response);

        mockMvc.perform(get("/api/stores/{storeId}", storeId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("넥스트 맛집"))
                .andExpect(jsonPath("$.data.fullAddress").value(containsString("역삼동")))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 목록 조회 - 성공")
    void getStoreList() throws Exception {
        StoreListResponse store = new StoreListResponse(
                UUID.randomUUID(), "치킨집", "서울 강남", new BigDecimal("4.5"), 50, "치킨", LocalDateTime.now()
        );
        PageImpl<StoreListResponse> pageResponse = new PageImpl<>(List.of(store), PageRequest.of(0, 10), 1);

        given(storeService.getStoreList(any(StoreSearchCondition.class), any(Pageable.class)))
                .willReturn(pageResponse);

        mockMvc.perform(get("/api/stores")
                        .param("name", "치킨")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 정보 수정 - 성공")
    void updateStore() throws Exception {
        // 1. 테스트 데이터 준비
        UUID storeId = UUID.randomUUID();
        StoreUpdateRequest request = new StoreUpdateRequest(
                "수정된 가게", "서울", "강남구", "역삼동", "수정주소", List.of(UUID.randomUUID())
        );
        StoreResponse response = new StoreResponse(
                storeId, "수정된 가게", "사장님", "서울 강남구 역삼동 수정주소",
                new BigDecimal("4.8"), 100, List.of("치킨")
        );

        // [중요] 매처 밖에서 미리 값을 추출합니다.
        Long userId = mockUser.userId();

        // 2. Mock 설정 (매처를 중첩해서 사용하지 마세요!)
        given(storeService.updateStore(
                eq(storeId),
                any(StoreUpdateRequest.class),
                eq(userId) // 깔끔하게 eq() 하나만 사용
        )).willReturn(response);

        // 3. 실행 및 검증
        mockMvc.perform(patch("/api/stores/{storeId}", storeId)
                        .with(user(principal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 가게"))
                .andDo(print());
    }

    @Test
    @DisplayName("가게 삭제 - 성공")
    void deleteStore() throws Exception {
        UUID storeId = UUID.randomUUID();

        mockMvc.perform(delete("/api/stores/{storeId}", storeId)
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("가게가 성공적으로 삭제되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 가게_평점_리뷰개수_조회() throws Exception {
        //given
        UUID storeId = UUID.randomUUID();
        given(storeReviewService.getStoreRatingAvg(storeId)).willReturn(4.5);
        given(storeReviewService.getStoreReviewCount(storeId)).willReturn(100);

        //when & then
        mockMvc.perform(get("/api/stores/{storeId}/summary", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.ratingAvg").value(4.5))
            .andExpect(jsonPath("$.data.reviewCount").value(100));
    }
}
