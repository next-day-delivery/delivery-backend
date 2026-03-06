package com.nextdaydelivery.ai_response.infrastructure.dto;

public record AiGenerationResult(
        String productName,
        String prompt,
        String content
) {
}
