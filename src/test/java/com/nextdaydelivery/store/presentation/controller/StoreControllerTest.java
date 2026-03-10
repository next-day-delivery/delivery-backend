package com.nextdaydelivery.store.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.nextdaydelivery.store.presentation.dto.request.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.request.StoreUpdateRequest;
import com.nextdaydelivery.store.presentation.dto.response.StoreCreationResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreListResponse;
import com.nextdaydelivery.store.presentation.dto.response.StoreResponse;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(controllers = {StoreController.class})
public class StoreControllerTest extends ControllerTestSupport {

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
                .andDo(document("store-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("가게 이름"),
                                fieldWithPath("sigungu").description("군/구"),
                                fieldWithPath("sido").description("시/도"),
                                fieldWithPath("dong").description("읍/면/동"),
                                fieldWithPath("detailAddress").description("상세 주소"),
                                fieldWithPath("categoryNames").description("카테고리 이름 목록")
                        ),
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data.storeId").description("생성된 가게 ID"),
                                fieldWithPath("data.name").description("가게 이름"),
                                fieldWithPath("data.sigungu").description("군/구"),
                                fieldWithPath("data.sido").description("시/도"),
                                fieldWithPath("data.dong").description("읍/면/동"),
                                fieldWithPath("data.detailAddress").description("상세 주소"),
                                fieldWithPath("data.categoryIds").description("연결된 카테고리 ID 목록")
                        )
                ));
    }

    @Test
    @DisplayName("가게 상세 조회 - 성공")
    void getStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreResponse response = new StoreResponse(
                storeId, "넥스트 맛집", "사장님", "서울특별시 강남구 역삼동 테헤란로 123",
                new BigDecimal("4.8"), 100, List.of("치킨", "한식")
        );

        given(storeService.getStore(storeId)).willReturn(response);

        mockMvc.perform(get("/api/stores/{storeId}", storeId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andDo(document("store-get-detail",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("storeId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data.storeId").description("가게 ID"),
                                fieldWithPath("data.name").description("가게 이름"),
                                fieldWithPath("data.ownerNickname").description("점주 닉네임"),
                                fieldWithPath("data.fullAddress").description("전체 주소"),
                                fieldWithPath("data.ratingAvg").description("평균 평점"),
                                fieldWithPath("data.reviewCount").description("리뷰 개수"),
                                fieldWithPath("data.categoryNames").description("카테고리 목록")
                        )
                ));
    }

    @Test
    @DisplayName("가게 목록 조회 - 성공")
    void getStoreList() throws Exception {
        StoreListResponse store = new StoreListResponse(
                UUID.randomUUID(), "치킨집", "서울 강남구", new BigDecimal("4.5"), 50, "치킨", LocalDateTime.now()
        );
        PageImpl<StoreListResponse> pageResponse = new PageImpl<>(List.of(store), PageRequest.of(0, 10), 1);

        given(storeService.getStoreList(any(StoreSearchCondition.class), any(Pageable.class)))
                .willReturn(pageResponse);

        mockMvc.perform(get("/api/stores")
                        .param("name", "치킨")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andDo(document("store-get-list",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("name").description("가게 이름 검색어").optional(),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        // getStoreList 테스트의 responseFields 부분 수정
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data.content[].storeId").description("가게 ID"),
                                fieldWithPath("data.content[].name").description("가게 이름"),
                                fieldWithPath("data.content[].region").description("지역 요약"),
                                fieldWithPath("data.content[].ratingAvg").description("평균 평점"),
                                fieldWithPath("data.content[].reviewCount").description("리뷰 개수"),
                                fieldWithPath("data.content[].mainCategory").description("대표 카테고리"),
                                fieldWithPath("data.content[].createdAt").description("등록 일시"),

                                // 페이징 상세 정보 (에러 해결 핵심 부분)
                                fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 정보 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("미정렬 여부"),
                                fieldWithPath("data.pageable.offset").description("해당 페이지 시작점"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("미페이징 여부"),

                                fieldWithPath("data.totalElements").description("전체 요소 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("현재 페이지 번호"),

                                // 하단 sort 객체 (pageable 내부 sort와 별개로 바깥에도 존재함)
                                fieldWithPath("data.sort.empty").description("정렬 정보 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("미정렬 여부"),

                                fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.empty").description("비어 있음 여부")
                        )
                ));
    }

    @Test
    @DisplayName("가게 정보 수정 - 성공")
    void updateStore() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreUpdateRequest request = new StoreUpdateRequest(
                "수정된 가게", "서울", "강남구", "역삼동", "수정주소", List.of(UUID.randomUUID())
        );
        StoreResponse response = new StoreResponse(
                storeId, "수정된 가게", "사장님", "서울 강남구 역삼동 수정주소",
                new BigDecimal("4.8"), 100, List.of("치킨")
        );

        given(storeService.updateStore(eq(storeId), any(StoreUpdateRequest.class), eq(1L)))
                .willReturn(response);

        mockMvc.perform(patch("/api/stores/{storeId}", storeId)
                        .with(user(principal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("store-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("storeId").description("수정할 가게 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 가게 이름"),
                                fieldWithPath("sido").description("수정할 시/도"),
                                fieldWithPath("sigungu").description("수정할 군/구"),
                                fieldWithPath("dong").description("수정할 읍/면/동"),
                                fieldWithPath("detailAddress").description("수정할 상세 주소"),
                                fieldWithPath("categoryIds").description("수정할 카테고리 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data.storeId").description("가게 ID"),
                                fieldWithPath("data.name").description("수정된 가게 이름"),
                                fieldWithPath("data.ownerNickname").description("점주 닉네임"),
                                fieldWithPath("data.fullAddress").description("전체 주소"),
                                fieldWithPath("data.ratingAvg").description("평균 평점"),
                                fieldWithPath("data.reviewCount").description("리뷰 개수"),
                                fieldWithPath("data.categoryNames").description("수정된 카테고리 목록")
                        )
                ));
    }

    @Test
    @DisplayName("가게 삭제 - 성공")
    void deleteStore() throws Exception {
        UUID storeId = UUID.randomUUID();

        mockMvc.perform(delete("/api/stores/{storeId}", storeId)
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("store-delete",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("storeId").description("삭제할 가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data").description("삭제 완료 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("가게 평점 및 리뷰 요약 조회 - 성공")
    void getStoreSummary() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        given(storeReviewService.getStoreRatingAvg(storeId)).willReturn(4.5);
        given(storeReviewService.getStoreReviewCount(storeId)).willReturn(100);

        // when & then
        mockMvc.perform(get("/api/stores/{storeId}/summary", storeId)
                        .with(user(principal))) // 인증 정보가 필요한 경우 유지
                .andExpect(status().isOk())
                .andDo(document("store-get-summary",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("storeId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("result").description("결과 상태"),
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("timestamp").description("발생 시간"),
                                fieldWithPath("data.ratingAvg").description("평균 평점"),
                                fieldWithPath("data.reviewCount").description("총 리뷰 개수")
                        )
                ));
    }
}
