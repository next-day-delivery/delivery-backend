package com.nextdaydelivery.product.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.product.application.ProductService;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ProductController.class})
@AutoConfigureRestDocs
class ProductControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private AuthUserDto mockUser;
    private PrincipalDetails principal;

    @BeforeEach
    void setUp() {
        mockUser = new AuthUserDto(1L, UserRole.OWNER);
        principal = new PrincipalDetails(mockUser);
    }

    @Test
    @DisplayName("허용된 pageSize(30) 요청 시 그대로 전달된다")
    void pageable_size_30() throws Exception {

        when(productService.searchProducts(any(), any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(
                        new ProductResponse(UUID.randomUUID(), "상품", "설명", 1000)
                )));

        mockMvc.perform(get("/api/products/search")
                        .param("page", "0")
                        .param("size", "30")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                any(), any(), any(), any(), any(),
                argThat(pageable ->
                        pageable.getPageNumber() == 0 &&
                                pageable.getPageSize() == 30
                )
        );
    }

    @Test
    @DisplayName("허용되지 않은 pageSize(100) 요청 시 10으로 변경된다")
    void pageable_invalid_size() throws Exception {

        when(productService.searchProducts(any(), any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of()));

        mockMvc.perform(get("/api/products/search")
                        .param("page", "0")
                        .param("size", "100")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                any(), any(), any(), any(), any(),
                argThat(pageable ->
                        pageable.getPageSize() == 10
                )
        );
    }

    @Test
    @DisplayName("pageSize 미입력 시 기본값 10")
    void pageable_default() throws Exception {

        when(productService.searchProducts(any(), any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of()));

        mockMvc.perform(get("/api/products/search")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk());

        verify(productService).searchProducts(
                any(), any(), any(), any(), any(),
                argThat(pageable ->
                        pageable.getPageSize() == 10
                )
        );
    }

    @Test
    @DisplayName("상품 검색 API - 이름/가격 필터 + 페이징")
    void searchProducts_restdocs() throws Exception {
        UUID storeId = UUID.randomUUID();

        ProductResponse p1 = new ProductResponse(UUID.randomUUID(), "순살양념치킨", "한 입에 쏙! 비법 소스가 듬뿍 배어든 겉바속촉 인생 순살양념치킨",
                23000);
        ProductResponse p2 = new ProductResponse(UUID.randomUUID(), "사천양념치킨",
                "입안 가득 퍼지는 알싸한 풍미! 멈출 수 없는 화끈한 유혹, 사천양념치킨", 23000);
        ProductResponse p3 = new ProductResponse(UUID.randomUUID(), "옛날통닭치킨",
                "겉은 바삭 속은 촉촉! 얇은 껍질 속 육즙이 팡 터지는 추억의 그 맛, 정통 옛날통닭", 15000);
        ProductResponse p4 = new ProductResponse(UUID.randomUUID(), "마늘통닭치킨",
                "알싸한 마늘 소스가 듬뿍! 겉바속촉 통닭과 환상 조화를 이루는 중독적인 풍미.", 20000);
        ProductResponse p5 = new ProductResponse(UUID.randomUUID(), "불닭치킨", "한 번 맛보면 멈출 수 없는 강렬한 매운맛, 중독성 끝판왕 불닭치킨!",
                18000);

        Slice<ProductResponse> slice = new SliceImpl<>(List.of(p1, p2, p3, p4, p5), PageRequest.of(0, 50), false);

        given(productService.searchProducts(
                nullable(String.class),
                nullable(Integer.class),
                nullable(Integer.class),
                nullable(UUID.class),
                eq(storeId),
                any(Pageable.class)
        )).willReturn(slice);

        mockMvc.perform(get("/api/products/search")
                        .param("storeId", storeId.toString())
                        .param("page", "0")
                        .param("size", "50")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andDo(document("product-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("storeId").description("가게 ID (UUID)"),
                                parameterWithName("name").optional().description("상품 이름 검색"),
                                parameterWithName("minPrice").optional().description("최소 가격"),
                                parameterWithName("maxPrice").optional().description("최대 가격"),
                                parameterWithName("cursor").optional().description("커서 ID (UUID)"),
                                parameterWithName("page").optional().description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").optional().description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("result").description("요청 결과 (SUCCESS/FAIL)"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간"),

                                fieldWithPath("data.content[]").description("상품 리스트"),
                                fieldWithPath("data.content[].productId").description("상품 ID"),
                                fieldWithPath("data.content[].productName").description("상품 이름"),
                                fieldWithPath("data.content[].productDetail").description("상품 설명"),
                                fieldWithPath("data.content[].price").description("상품 가격"),

                                fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.offset").description("전체 오프셋"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("비페이징 여부"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부"),

                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("페이지 번호"),
                                fieldWithPath("data.sort.empty").description("정렬 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                                fieldWithPath("data.empty").description("페이지가 비어있는지 여부")
                        )
                ));
    }


    @Test
    @DisplayName("상품 검색 - 기본 페이징, 이름 필터 없이")
    void searchProducts_defaultPaging() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "상품", "설명", 10000);

        Slice<ProductResponse> slice = new SliceImpl<>(
                List.of(response)
        );

        given(productService.searchProducts(
                nullable(String.class),
                nullable(Integer.class),
                nullable(Integer.class),
                nullable(UUID.class),
                eq(storeId),
                any(Pageable.class)
        )).willReturn(slice);

        mockMvc.perform(get("/api/products/search")
                        .param("storeId", storeId.toString())   // 필수
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.data.content[0].productName").value("상품"))
                .andExpect(jsonPath("$.data.content[0].productDetail").value("설명"))
                .andExpect(jsonPath("$.data.content[0].price").value(10000));

        verify(productService).searchProducts(
                nullable(String.class),
                nullable(Integer.class),
                nullable(Integer.class),
                nullable(UUID.class),
                eq(storeId),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("상품 검색 - 이름 필터 적용")
    void searchProducts_withNameFilter() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "피자", "맛있는 피자", 30000);

        Slice<ProductResponse> slice = new SliceImpl<>(
                List.of(response)
        );

        String searchName = "피자";

        given(productService.searchProducts(
                eq(searchName),
                nullable(Integer.class),
                nullable(Integer.class),
                nullable(UUID.class),
                eq(storeId),                 // storeId 필수
                any(Pageable.class)
        )).willReturn(slice);

        mockMvc.perform(get("/api/products/search")
                        .param("storeId", storeId.toString())   // 반드시 전달
                        .param("name", searchName)
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productName").value("피자"))
                .andExpect(jsonPath("$.data.content[0].productDetail").value("맛있는 피자"))
                .andExpect(jsonPath("$.data.content[0].price").value(30000));

        verify(productService).searchProducts(
                eq(searchName),
                nullable(Integer.class),
                nullable(Integer.class),
                nullable(UUID.class),
                eq(storeId),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("상품 검색 - 가격 범위 필터 적용")
    void searchProducts_withPriceFilter() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "햄버거", "맛있는 햄버거", 15000);

        Slice<ProductResponse> slice = new SliceImpl<>(
                List.of(response)
        );

        int minPrice = 10000;
        int maxPrice = 20000;

        given(productService.searchProducts(
                nullable(String.class),
                eq(minPrice),
                eq(maxPrice),
                nullable(UUID.class),
                eq(storeId),              // storeId는 필수
                any(Pageable.class)
        )).willReturn(slice);

        mockMvc.perform(get("/api/products/search")
                        .param("storeId", storeId.toString())   // 반드시 전달
                        .param("minPrice", String.valueOf(minPrice))
                        .param("maxPrice", String.valueOf(maxPrice))
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productName").value("햄버거"))
                .andExpect(jsonPath("$.data.content[0].productDetail").value("맛있는 햄버거"))
                .andExpect(jsonPath("$.data.content[0].price").value(15000));

        verify(productService).searchProducts(
                nullable(String.class),
                eq(minPrice),
                eq(maxPrice),
                nullable(UUID.class),
                eq(storeId),
                any(Pageable.class)
        );
    }


    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_Success() throws Exception {

        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        ProductCreateRequest request =
                new ProductCreateRequest(storeId, "치킨", "정말 맛있는 치킨", 20000, false);

        ProductResponse response =
                new ProductResponse(productId, "치킨", "정말 맛있는 치킨", 20000);

        given(productService.create(any()))
                .willReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()))
                .andExpect(jsonPath("$.data.productName").value("치킨"))
                .andExpect(jsonPath("$.data.productDetail").value("정말 맛있는 치킨"))
                .andExpect(jsonPath("$.data.price").value(20000))
                .andDo(document("product-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("storeId")
                                        .type(JsonFieldType.STRING)
                                        .description("가게 ID (UUID)"),

                                fieldWithPath("productName")
                                        .type(JsonFieldType.STRING)
                                        .description("상품 이름"),

                                fieldWithPath("productDetail")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상품 설명"),

                                fieldWithPath("price")
                                        .type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),

                                fieldWithPath("useAi")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("AI 상품 설명 생성 여부")
                        ),

                        responseFields(
                                fieldWithPath("result")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 결과 (SUCCESS / FAIL)"),

                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드"),

                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지"),

                                fieldWithPath("timestamp")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 시간"),

                                fieldWithPath("data")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),

                                fieldWithPath("data.productId")
                                        .type(JsonFieldType.STRING)
                                        .description("상품 ID"),

                                fieldWithPath("data.productName")
                                        .type(JsonFieldType.STRING)
                                        .description("상품 이름"),

                                fieldWithPath("data.productDetail")
                                        .type(JsonFieldType.STRING)
                                        .description("상품 설명"),

                                fieldWithPath("data.price")
                                        .type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        )
                ));
    }

    @Test
    @DisplayName("상품 생성 실패 - 상품명 빈 값")
    void createProduct_Fail_EmptyName() throws Exception {
        ProductCreateRequest request =
                new ProductCreateRequest(UUID.randomUUID(), "", "맛있는 치킨", 20000, false);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductUpdateRequest request =
                new ProductUpdateRequest(productId, "수정된 설명", "수정된 이름", 25000);

        ProductResponse response =
                new ProductResponse(productId, "수정된 이름", "수정된 설명", 25000);

        given(productService.update(any())).willReturn(response);

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("product-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type(JsonFieldType.STRING).description("상품 ID"),
                                fieldWithPath("productDetail").type(JsonFieldType.STRING).description("수정된 상품 설명"),
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("수정된 상품 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("수정된 상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.productId").type(JsonFieldType.STRING).description("상품 ID"),
                                fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("data.productDetail").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                ));
    }

    @Test
    @DisplayName("상품 단건 조회 성공")
    void readById() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "햄버거", "맛있는 햄버거", 15000);

        given(productService.readById(productId)).willReturn(response);

        mockMvc.perform(get("/api/products/{id}", productId)
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("product-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.productId").type(JsonFieldType.STRING).description("상품 ID"),
                                fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("data.productDetail").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                ));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteById() throws Exception {

        UUID productId = UUID.randomUUID();

        given(productService.deleteById(productId))
                .willReturn(productId);

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").value(productId.toString()));
    }

    @Test
    @DisplayName("상품 숨김 처리 성공")
    void hideById() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductResponse response = new ProductResponse(productId, "상품", "설명", 10000);

        given(productService.hideById(productId, String.valueOf(principal.getAuthUserDto().userId()))).willReturn(response);

        mockMvc.perform(patch("/api/products/{id}", productId)
                        .with(user(principal)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("product-hide",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                                fieldWithPath("data.productId").type(JsonFieldType.STRING).description("숨김 처리된 상품 ID"),
                                fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("data.productDetail").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                ));
    }
}