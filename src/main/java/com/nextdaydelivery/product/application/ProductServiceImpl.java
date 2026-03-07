package com.nextdaydelivery.product.application;

import com.nextdaydelivery.ai_response.application.AiClient;
import com.nextdaydelivery.ai_response.application.AiEventPublisher;
import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.repository.ProductRepository;
import com.nextdaydelivery.product.exception.ProductErrorCode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AiClient aiClient;
    private final AiEventPublisher aiEventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ProductResponse create(ProductCreateRequest createRequest) {
        AiGenerationResult aiResult = generateProductDetailIfNeeded(createRequest);
        String productDetail = resolveProductDetail(createRequest, aiResult);
        ProductResponse response = saveProduct(createRequest, productDetail);
        publishAiEvent(aiResult);

        return response;
    }

    @Override
    @Transactional
    public ProductResponse update(ProductUpdateRequest updateRequest) {
        Product product = findById(updateRequest.productId());

        if (updateRequest.productName() != null) {
            product.updateProductName(updateRequest.productName());
        }

        if (updateRequest.productDetail() != null) {
            product.updateProductDetail(updateRequest.productDetail());
        }

        if (updateRequest.price() != null) {
            product.updatePrice(updateRequest.price());
        }

        return response(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> readAll() {
        return productRepository.findAll()
                .stream()
                .filter(product -> !product.isHidden())
                .map(this::response)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse readById(UUID id) {
        Product product = findById(id);

        return response(product);
    }

    @Override
    @Transactional
    public UUID deleteById(UUID id) {
        Product product = findById(id);
        productRepository.delete(product);

        return id;
    }

    @Override
    @Transactional
    public ProductResponse hideById(UUID id) {
        Product product = findById(id);
        product.hide();

        return response(product);
    }

    private AiGenerationResult generateProductDetailIfNeeded(ProductCreateRequest request) {
        if (!request.useAi()) {
            return null;
        }
        return aiClient.generateProductDetail(request.productName());
    }

    private String resolveProductDetail(ProductCreateRequest request, AiGenerationResult result) {
        return result != null ? result.content() : request.productDetail();
    }

    private ProductResponse saveProduct(ProductCreateRequest createRequest, String productDetail) {
        return transactionTemplate.execute(status -> {
            Product product = Product.ofCreateRequest(createRequest, productDetail);
            productRepository.save(product);
            return response(product);
        });
    }

    private void publishAiEvent(AiGenerationResult result) {
        if (isResultNull(result)) {
            aiEventPublisher.publishEvent(AiUsedEvent.from(result));
        }
    }

    private boolean isResultNull(AiGenerationResult result) {
        return result != null;
    }

    private Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private ProductResponse response(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getProductDetail(),
                product.getPrice());
    }
}
