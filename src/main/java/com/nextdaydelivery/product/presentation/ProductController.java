package com.nextdaydelivery.product.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.product.application.ProductService;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public CommonResponse<ProductResponse> create(@Valid @RequestBody ProductCreateRequest createRequest) {
        return CommonResponse.onSuccess(HttpStatus.CREATED, productService.create(createRequest));
    }

    @PatchMapping
    public CommonResponse<ProductResponse> update(
            @Valid @RequestBody ProductUpdateRequest updateRequest) {
        return CommonResponse.onSuccess(HttpStatus.OK, productService.update(updateRequest));
    }

    @GetMapping
    public CommonResponse<List<ProductResponse>> readAll(Pageable pageable) {
        return CommonResponse.onSuccess(HttpStatus.OK, productService.readAll(pageable));
    }

    @GetMapping("/{id}")
    public CommonResponse<ProductResponse> readById(@PathVariable UUID id) {
        return CommonResponse.onSuccess(HttpStatus.OK, productService.readById(id));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<UUID> deleteById(@PathVariable UUID id) {
        return CommonResponse.onSuccess(HttpStatus.OK, productService.deleteById(id));
    }

    @PatchMapping("/{id}")
    public CommonResponse<ProductResponse> hideById(@PathVariable UUID id) {
        return CommonResponse.onSuccess(HttpStatus.OK, productService.hideById(id));
    }

    @GetMapping("/search")
    public CommonResponse<Slice<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) UUID cursor,
            Pageable pageable
    ) {
        return CommonResponse.onSuccess(
                productService.searchProducts(name, minPrice, maxPrice, cursor, pageable)
        );
    }
}
