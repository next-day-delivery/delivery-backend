package com.nextdaydelivery.store.presentation.controller;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.store.domain.service.StoreService;
import com.nextdaydelivery.store.presentation.dto.StoreCreationRequest;
import com.nextdaydelivery.store.presentation.dto.StoreCreationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public CommonResponse<StoreCreationResponse> createStore(@Valid @RequestBody StoreCreationRequest request) {

        StoreCreationResponse response = storeService.createStore(request);
        return CommonResponse.onSuccess(response);
    }

}
