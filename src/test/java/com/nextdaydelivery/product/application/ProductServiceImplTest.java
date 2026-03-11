package com.nextdaydelivery.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextdaydelivery.ai_response.application.AiClient;
import com.nextdaydelivery.ai_response.application.AiEventPublisher;
import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.repository.ProductRepository;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private StoreRepository storeRepository;
    private AiClient aiClient;
    private AiEventPublisher aiEventPublisher;
    private TransactionTemplate transactionTemplate;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        storeRepository = mock(StoreRepository.class);
        aiClient = mock(AiClient.class);
        aiEventPublisher = mock(AiEventPublisher.class);
        transactionTemplate = mock(TransactionTemplate.class);

        productService = new ProductServiceImpl(
                productRepository, storeRepository, aiClient, aiEventPublisher, transactionTemplate
        );
    }

    @Test
    @DisplayName("상품 생성 - AI 사용")
    void createProduct_withAi() {
        UUID storeId = UUID.randomUUID();
        ProductCreateRequest request = new ProductCreateRequest(storeId, "치킨", "", 20000, true);

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        AiGenerationResult aiResult = new AiGenerationResult("치킨","질문","AI-generated 내용");
        when(aiClient.generateProductDetail("치킨")).thenReturn(aiResult);

        when(transactionTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    Product product = Product.ofCreateRequest(request, aiResult.content(), store);
                    when(productRepository.save(any())).thenReturn(product);
                    return new ProductResponse(product.getProductId(), product.getProductName(), product.getProductDetail(), product.getPrice());
                });

        ProductResponse response = productService.create(request);

        assertThat(response.productName()).isEqualTo("치킨");
        assertThat(response.productDetail()).isEqualTo("AI-generated 내용");
        assertThat(response.price()).isEqualTo(20000);

        verify(aiClient).generateProductDetail("치킨");
    }

    @Test
    @DisplayName("상품 생성 - AI 미사용")
    void createProduct_withoutAi() {
        UUID storeId = UUID.randomUUID();
        ProductCreateRequest request = new ProductCreateRequest(storeId, "피자", "수제 피자", 15000, false);

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        when(transactionTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    Product product = Product.ofCreateRequest(request, request.productDetail(), store);
                    when(productRepository.save(any())).thenReturn(product);
                    return new ProductResponse(product.getProductId(), product.getProductName(), product.getProductDetail(), product.getPrice());
                });

        ProductResponse response = productService.create(request);

        assertThat(response.productDetail()).isEqualTo("수제 피자");
        verify(aiClient, never()).generateProductDetail(any());
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductUpdateRequest request = new ProductUpdateRequest(productId, "새 설명", "새 이름", 25000);

        ProductResponse response = productService.update(request);

        verify(product).updateProductName("새 이름");
        verify(product).updateProductDetail("새 설명");
        verify(product).updatePrice(25000);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("상품 숨김 처리")
    void hideProduct() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = productService.hideById(productId, "userid");

        verify(product).hide("userid");
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        UUID deletedId = productService.deleteById(productId);

        verify(productRepository).delete(product);
        assertThat(deletedId).isEqualTo(productId);
    }
}