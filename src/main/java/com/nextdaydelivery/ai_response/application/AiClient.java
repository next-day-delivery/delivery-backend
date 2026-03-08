package com.nextdaydelivery.ai_response.application;

import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;

public interface AiClient {

    AiGenerationResult generateProductDetail(String productName);
}
