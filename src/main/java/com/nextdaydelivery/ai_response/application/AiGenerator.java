package com.nextdaydelivery.ai_response.application;

import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;

public interface AiGenerator {

    AiGenerationResult generateProductDetail(String productName);
}
