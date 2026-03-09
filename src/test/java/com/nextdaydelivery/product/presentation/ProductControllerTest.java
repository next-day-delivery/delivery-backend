package com.nextdaydelivery.product.presentation;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
    @DisplayName("상품 생성 성공")
    void createProduct_Success() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductCreateRequest request =
                new ProductCreateRequest(UUID.randomUUID(), "치킨", "맛있는 치킨", 20000, false);

        ProductResponse response =
                new ProductResponse(productId, "치킨", "맛있는 치킨", 20000);

        // 서비스 동작 mocking
        BDDMockito.given(productService.create(any()))
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

        BDDMockito.given(productService.update(any()))
                .willReturn(response);

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("수정된 이름"))
                .andExpect(jsonPath("$.data.price").value(25000));
    }

    @Test
    @DisplayName("상품 전체 조회 성공")
    void readAll() throws Exception {
        ProductResponse response =
                new ProductResponse(UUID.randomUUID(), "피자", "맛있는 피자", 30000);

        BDDMockito.given(productService.readAll())
                .willReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].productName").value("피자"));
    }

    @Test
    @DisplayName("상품 단건 조회 성공")
    void readById() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductResponse response =
                new ProductResponse(productId, "햄버거", "맛있는 햄버거", 15000);

        BDDMockito.given(productService.readById(productId))
                .willReturn(response);

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteById() throws Exception {

        UUID productId = UUID.randomUUID();

        BDDMockito.given(productService.deleteById(productId))
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

        BDDMockito.given(productService.hideById(productId))
                .willReturn(response);

        mockMvc.perform(patch("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.toString()));
    }
}