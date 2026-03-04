package com.nextdaydelivery.product.application;

import com.nextdaydelivery.ai_response.application.AiEventPublisher;
import com.nextdaydelivery.ai_response.application.AiGenerator;
import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.repository.ProductRepository;
import com.nextdaydelivery.product.exception.ProductNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AiGenerator aiGenerator;
    private final AiEventPublisher aiEventPublisher;

    @Transactional
    public ProductResponse create(ProductCreateRequest createRequest) {
        String productDetail = createRequest.productDetail();

        if (createRequest.useAi()) {
            AiGenerationResult result = aiGenerator.generateProductDetail(createRequest.productName());
            productDetail = result.content();

            aiEventPublisher.publishEvent(AiUsedEvent.from(result));
        }

        Product product = Product.ofCreateRequest(
                createRequest,
                productDetail
        );

        productRepository.save(product);

        return response(product);
    }

    @Override
    @Transactional
    public ProductResponse update(ProductUpdateRequest updateRequest) {
        Product product = productRepository.findById(updateRequest.id())
                .orElseThrow(ProductNotFoundException::new);

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
        Product product = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        return response(product);
    }

    @Override
    @Transactional
    public UUID deleteById(UUID id) {
        productRepository.deleteById(id);

        return id;
    }

    @Override
    @Transactional
    public ProductResponse hideById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        product.hide();

        return response(product);
    }


    private ProductResponse response(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getProductDetail(),
                product.getPrice());
    }
}
