package com.nextdaydelivery.cart.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.cart.application.service.CartService;
import com.nextdaydelivery.cart.domain.enums.CartStatus;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPostCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResGetCartItemsDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPostCartItemDto;
import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.domain.error.CartErrorCode;
import com.nextdaydelivery.global.domain.error.GlobalErrorCode;
import com.nextdaydelivery.global.config.SecurityConfig;
import com.nextdaydelivery.global.dto.CommonResponse.Result;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
class CartControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    private PrincipalDetails customerPrincipal;
    private PrincipalDetails ownerPrincipal;

    @BeforeEach
    void setUp() {
        customerPrincipal = new PrincipalDetails(new AuthUserDto(1L, UserRole.CUSTOMER));
        ownerPrincipal = new PrincipalDetails(new AuthUserDto(2L, UserRole.OWNER));
    }

    @Test
    @DisplayName("장바구니 품목 추가 성공")
    void addCartItem_success() throws Exception {
        UUID cartId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ReqPostCartItemDto request = new ReqPostCartItemDto(productId, 2L);
        ResPostCartItemDto response = new ResPostCartItemDto(cartId, storeId, productId, 2L, CartStatus.ACTIVE);

        given(cartService.addCartItem(eq(1L), any(ReqPostCartItemDto.class))).willReturn(response);

        mockMvc.perform(post("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId.toString()))
                .andExpect(jsonPath("$.storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(2L))
                .andExpect(jsonPath("$.cartStatus").value(CartStatus.ACTIVE.name()))
                .andDo(document("cart-add-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").description("장바구니에 담을 상품 ID"),
                                fieldWithPath("quantity").description("추가할 수량")
                        ),
                        responseFields(
                                fieldWithPath("cartId").description("장바구니 ID"),
                                fieldWithPath("storeId").description("가게 ID"),
                                fieldWithPath("productId").description("상품 ID"),
                                fieldWithPath("quantity").description("장바구니 내 수량"),
                                fieldWithPath("cartStatus").description("장바구니 상태")
                        )))
                .andDo(print());

        verify(cartService).addCartItem(eq(1L), any(ReqPostCartItemDto.class));
    }

    @Test
    @DisplayName("장바구니 품목 추가 실패 - 상품 없음")
    void addCartItem_fail_productNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        ReqPostCartItemDto request = new ReqPostCartItemDto(productId, 2L);

        given(cartService.addCartItem(eq(1L), any(ReqPostCartItemDto.class)))
                .willThrow(new BusinessException(CartErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(post("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(CartErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(CartErrorCode.PRODUCT_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 품목 추가 실패 - 수량 검증 실패")
    void addCartItem_fail_validation_quantityZero() throws Exception {
        UUID productId = UUID.randomUUID();
        ReqPostCartItemDto request = new ReqPostCartItemDto(productId, 0L);

        mockMvc.perform(post("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_INPUT_VALUE.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value("quantity"))
                .andDo(print());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("장바구니 품목 추가 실패 - productId 누락")
    void addCartItem_fail_validation_productIdNull() throws Exception {
        String invalidRequest = """
                {
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_INPUT_VALUE.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value("productId"))
                .andDo(print());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("활성 장바구니 조회 성공 - 품목 있음")
    void getActiveCartItems_success_withItems() throws Exception {
        UUID cartId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();

        ResGetCartItemsDto response = new ResGetCartItemsDto(
                cartId,
                storeId,
                CartStatus.ACTIVE,
                List.of(
                        new ResGetCartItemsDto.CartItemDetail(firstProductId, "후라이드 치킨", 21000, 2L),
                        new ResGetCartItemsDto.CartItemDetail(secondProductId, "콜라", 2000, 1L)
                )
        );

        given(cartService.getActiveCartItems(1L)).willReturn(response);

        mockMvc.perform(get("/api/carts")
                        .with(user(customerPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId.toString()))
                .andExpect(jsonPath("$.storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.cartStatus").value(CartStatus.ACTIVE.name()))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].productId").value(firstProductId.toString()))
                .andExpect(jsonPath("$.items[0].productName").value("후라이드 치킨"))
                .andExpect(jsonPath("$.items[0].price").value(21000))
                .andExpect(jsonPath("$.items[0].quantity").value(2L))
                .andExpect(jsonPath("$.items[1].productId").value(secondProductId.toString()))
                .andExpect(jsonPath("$.items[1].productName").value("콜라"))
                .andDo(document("cart-get-active-items",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("cartId").description("장바구니 ID"),
                                fieldWithPath("storeId").description("가게 ID"),
                                fieldWithPath("cartStatus").description("장바구니 상태"),
                                fieldWithPath("items[]").description("장바구니 품목 목록"),
                                fieldWithPath("items[].productId").description("상품 ID"),
                                fieldWithPath("items[].productName").description("상품 이름"),
                                fieldWithPath("items[].price").description("상품 가격"),
                                fieldWithPath("items[].quantity").description("상품 수량")
                        )))
                .andDo(print());

        verify(cartService).getActiveCartItems(1L);
    }

    @Test
    @DisplayName("활성 장바구니 조회 성공 - 품목 없음")
    void getActiveCartItems_success_emptyItems() throws Exception {
        ResGetCartItemsDto response = new ResGetCartItemsDto(null, null, CartStatus.ACTIVE, List.of());
        given(cartService.getActiveCartItems(1L)).willReturn(response);

        mockMvc.perform(get("/api/carts")
                        .with(user(customerPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartStatus").value(CartStatus.ACTIVE.name()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andDo(print());

        verify(cartService).getActiveCartItems(1L);
    }

    @Test
    @DisplayName("활성 장바구니 조회 실패 - 사용자 없음")
    void getActiveCartItems_fail_userNotFound() throws Exception {
        given(cartService.getActiveCartItems(1L))
                .willThrow(new BusinessException(CartErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/carts")
                        .with(user(customerPrincipal)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(CartErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(CartErrorCode.USER_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 품목 수량 수정 성공")
    void updateCartItem_success() throws Exception {
        UUID cartId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ReqPatchCartItemDto request = new ReqPatchCartItemDto(5L);
        ResPatchCartItemDto response = new ResPatchCartItemDto(cartId, storeId, productId, 5L, CartStatus.ACTIVE);

        given(cartService.updateCartItem(eq(1L), eq(productId), any(ReqPatchCartItemDto.class))).willReturn(response);

        mockMvc.perform(patch("/api/carts/{productId}", productId)
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId.toString()))
                .andExpect(jsonPath("$.storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(5L))
                .andExpect(jsonPath("$.cartStatus").value(CartStatus.ACTIVE.name()))
                .andDo(document("cart-update-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("productId").description("수량을 변경할 상품 ID")
                        ),
                        requestFields(
                                fieldWithPath("quantity").description("변경할 수량")
                        ),
                        responseFields(
                                fieldWithPath("cartId").description("장바구니 ID"),
                                fieldWithPath("storeId").description("가게 ID"),
                                fieldWithPath("productId").description("상품 ID"),
                                fieldWithPath("quantity").description("변경된 수량"),
                                fieldWithPath("cartStatus").description("장바구니 상태")
                        )))
                .andDo(print());

        verify(cartService).updateCartItem(eq(1L), eq(productId), any(ReqPatchCartItemDto.class));
    }

    @Test
    @DisplayName("장바구니 품목 수량 수정 실패 - 품목 없음")
    void updateCartItem_fail_cartItemNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        ReqPatchCartItemDto request = new ReqPatchCartItemDto(3L);

        given(cartService.updateCartItem(eq(1L), eq(productId), any(ReqPatchCartItemDto.class)))
                .willThrow(new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND));

        mockMvc.perform(patch("/api/carts/{productId}", productId)
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(CartErrorCode.CART_ITEM_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(CartErrorCode.CART_ITEM_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 품목 수량 수정 실패 - 수량 검증 실패")
    void updateCartItem_fail_validation_quantityZero() throws Exception {
        UUID productId = UUID.randomUUID();
        ReqPatchCartItemDto request = new ReqPatchCartItemDto(0L);

        mockMvc.perform(patch("/api/carts/{productId}", productId)
                        .with(user(customerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.INVALID_INPUT_VALUE.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value("quantity"))
                .andDo(print());

        verifyNoInteractions(cartService);
    }

    @Test
    @DisplayName("장바구니 품목 삭제 성공")
    void deleteCartItem_success() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/api/carts/{productId}", productId)
                        .with(user(customerPrincipal))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(document("cart-delete-item",
                        pathParameters(
                                parameterWithName("productId").description("삭제할 상품 ID")
                        )))
                .andDo(print());

        verify(cartService).deleteCartItem(1L, productId);
    }

    @Test
    @DisplayName("장바구니 품목 삭제 실패 - 품목 없음")
    void deleteCartItem_fail_cartItemNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND))
                .when(cartService).deleteCartItem(1L, productId);

        mockMvc.perform(delete("/api/carts/{productId}", productId)
                        .with(user(customerPrincipal))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(CartErrorCode.CART_ITEM_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(CartErrorCode.CART_ITEM_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("활성 장바구니 전체 삭제 성공")
    void deleteCart_success() throws Exception {
        mockMvc.perform(delete("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(document("cart-delete-active-cart"))
                .andDo(print());

        verify(cartService).deleteActiveCart(1L);
    }

    @Test
    @DisplayName("활성 장바구니 전체 삭제 실패 - 활성 장바구니 없음")
    void deleteCart_fail_activeCartNotFound() throws Exception {
        doThrow(new BusinessException(CartErrorCode.ACTIVE_CART_NOT_FOUND))
                .when(cartService).deleteActiveCart(1L);

        mockMvc.perform(delete("/api/carts")
                        .with(user(customerPrincipal))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(CartErrorCode.ACTIVE_CART_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(CartErrorCode.ACTIVE_CART_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 API 접근 실패 - CUSTOMER 권한 아님")
    void cartApi_fail_forbidden_whenNotCustomerRole() throws Exception {
        UUID productId = UUID.randomUUID();
        ReqPostCartItemDto request = new ReqPostCartItemDto(productId, 1L);

        mockMvc.perform(post("/api/carts")
                        .with(user(ownerPrincipal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.FORBIDDEN.getMessage()))
                .andDo(print());

        verifyNoInteractions(cartService);
    }
}
