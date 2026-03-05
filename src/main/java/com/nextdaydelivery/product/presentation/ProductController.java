package com.nextdaydelivery.product.presentation;

import com.nextdaydelivery.product.application.ProductService;
import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.product.application.dto.request.ProductUpdateRequest;
import com.nextdaydelivery.product.application.dto.response.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest createRequest) {
        return new ResponseEntity<>(productService.create(createRequest), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<ProductResponse> update(
            @Valid @RequestBody ProductUpdateRequest updateRequest) {
        return new ResponseEntity<>(productService.update(updateRequest), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> readAll() {
        return new ResponseEntity<>(productService.readAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> readById(@PathVariable UUID id) {
        return new ResponseEntity<>(productService.readById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteById(@PathVariable UUID id) {
        return new ResponseEntity<>(productService.deleteById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> hideById(@PathVariable UUID id) {
        return new ResponseEntity<>(productService.hideById(id), HttpStatus.OK);
    }
}
