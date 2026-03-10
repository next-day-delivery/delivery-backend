package com.nextdaydelivery.store.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.request.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.request.StoreUpdateRequest;
import com.nextdaydelivery.store.presentation.dto.response.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
public class StoreControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private StoreReviewService storeReviewService;

    @Test
    @WithMockUser
    @DisplayName("가게 생성 성공 테스트")
    void createStore() throws Exception {
        StoreCreationRequest request = new StoreCreationRequest(
            "치킨나라", "강남구", "서울특별시", "역삼동", "테헤란로 123", List.of("치킨", "야식")
        );

        UUID generatedId = UUID.randomUUID();
        StoreCreationResponse response = new StoreCreationResponse(
            generatedId, "치킨나라", "강남구", "서울특별시", "역삼동", "테헤란로 123", List.of(UUID.randomUUID())
        );

        given(storeService.createStore(request)).willReturn(response);

        mockMvc.perform(post("/api/stores")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.storeId").value(generatedId.toString()))
            .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("가게 상세 조회 테스트")
    void getStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        // StoreResponse 필드 8개 맞춰서 생성
        StoreResponse response = new StoreResponse(
            storeId, "치킨나라", "사장님", "서울특별시 강남구 역삼동 테헤란로 123",
            BigDecimal.valueOf(4.5), 100, List.of("치킨")
        );

        given(storeService.getStore(storeId)).willReturn(response);

        mockMvc.perform(get("/api/stores/{storeId}", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.name").value("치킨나라"))
            .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("가게 정보 수정 테스트")
    void updateStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        // StoreUpdateRequest 필드 6개 (마지막 List<UUID> 추가)
        StoreUpdateRequest request = new StoreUpdateRequest(
            "수정된 치킨집", "서울", "강남구", "역삼", "번지", List.of(UUID.randomUUID())
        );
        StoreResponse response = new StoreResponse(
            storeId, "수정된 치킨집", "사장님", "서울 강남구 역삼 번지",
            BigDecimal.valueOf(4.5), 100, List.of("치킨")
        );

        given(storeService.updateStore(eq(storeId), any(StoreUpdateRequest.class))).willReturn(response);

        mockMvc.perform(patch("/api/stores/{storeId}", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("수정된 치킨집"))
            .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("가게 삭제(Soft Delete) 테스트")
    void deleteStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        String deletedBy = "admin_user";

        willDoNothing().given(storeService).deleteStore(storeId, deletedBy);

        mockMvc.perform(delete("/api/stores/{storeId}", storeId)
                .with(csrf())
                .param("deletedBy", deletedBy))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value("가게가 성공적으로 삭제되었습니다."))
            .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("가게 목록 조회(페이징) 테스트")
    void getStoreList() throws Exception {
        // StoreListResponse 필드 6개 맞춰서 생성
        StoreListResponse store1 = new StoreListResponse(
            UUID.randomUUID(), "치킨집1", "서울 강남구",
            BigDecimal.valueOf(4.0), 50, "치킨", LocalDateTime.now()
        );
        Page<StoreListResponse> pageResponse = new PageImpl<>(List.of(store1), PageRequest.of(0, 10), 1);

        given(storeService.getStoreList(any(StoreSearchCondition.class), any(Pageable.class)))
            .willReturn(pageResponse);

        mockMvc.perform(get("/api/stores")
                .param("name", "치킨")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content[0].name").value("치킨집1"))
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
