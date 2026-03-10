package com.nextdaydelivery.product.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.product.application.ProductService;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("허용된 pageSize(30) 요청 시 그대로 전달된다")
    void pageable_size_30() throws Exception {

        when(productService.readAll(any()))
                .thenReturn(List.of(
                        new ProductResponse(UUID.randomUUID(), "상품", "설명", 1000)
                ));

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "30"))
                .andExpect(status().isOk());

        verify(productService).readAll(argThat(pageable ->
                pageable.getPageNumber() == 0 &&
                        pageable.getPageSize() == 30
        ));
    }

    @Test
    @DisplayName("허용되지 않은 pageSize(100) 요청 시 10으로 변경된다")
    void pageable_invalid_size() throws Exception {

        when(productService.readAll(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());

        verify(productService).readAll(argThat(pageable ->
                pageable.getPageSize() == 10
        ));
    }

    @Test
    @DisplayName("pageSize 미입력 시 기본값 10")
    void pageable_default() throws Exception {

        when(productService.readAll(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        verify(productService).readAll(argThat(pageable ->
                pageable.getPageSize() == 10
        ));
    }

    @Test
    @DisplayName("상품 검색 - 기본 페이징, 이름 필터 없이")
    void searchProducts_defaultPaging() throws Exception {
        // Mock 데이터
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
                        .param("size", "10"))
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
                        .param("size", "10"))
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
                        .param("size", "10"))
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

        ProductCreateRequest request =
                new ProductCreateRequest(UUID.randomUUID(), "치킨", "맛있는 치킨", 20000, false);

        ProductResponse response =
                new ProductResponse(productId, "치킨", "맛있는 치킨", 20000);

        // 서비스 동작 mocking
        given(productService.create(any()))
                .willReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()))
                .andExpect(jsonPath("$.data.productName").value("치킨"))
                .andExpect(jsonPath("$.data.productDetail").value("맛있는 치킨"))
                .andExpect(jsonPath("$.data.price").value(20000));
    }

    @Test
    @DisplayName("상품 생성 실패 - 상품명 빈 값")
    void createProduct_Fail_EmptyName() throws Exception {
        ProductCreateRequest request =
                new ProductCreateRequest(UUID.randomUUID(), "", "맛있는 치킨", 20000, false);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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

        given(productService.update(any()))
                .willReturn(response);

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("수정된 이름"))
                .andExpect(jsonPath("$.data.price").value(25000));
    }

//    @Test
//    @DisplayName("상품 전체 조회 성공")
//    void readAll() throws Exception {
//        ProductResponse response =
//                new ProductResponse(UUID.randomUUID(), "피자", "맛있는 피자", 30000);
//
//        BDDMockito.given(productService.readAll())
//                .willReturn(List.of(response));
//
//        mockMvc.perform(get("/api/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.[0].productName").value("피자"));
//    }

    @Test
    @DisplayName("상품 단건 조회 성공")
    void readById() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "햄버거", "맛있는 햄버거", 15000);

        given(productService.readById(productId))
                .willReturn(response);

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteById() throws Exception {

        UUID productId = UUID.randomUUID();

        given(productService.deleteById(productId))
                .willReturn(productId);

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").value(productId.toString()));
    }

    @Test
    @DisplayName("상품 숨김 처리 성공")
    void hideById() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "상품", "설명", 10000);

        given(productService.hideById(productId))
                .willReturn(response);

        mockMvc.perform(patch("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()));
    }
}