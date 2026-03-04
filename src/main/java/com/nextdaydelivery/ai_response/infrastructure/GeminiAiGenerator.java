package com.nextdaydelivery.ai_response.infrastructure;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.nextdaydelivery.ai_response.application.AiGenerator;
import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiAiGenerator implements AiGenerator {

    private static final String PROMPT = "고객의 관심을 이끌 수 있도록 다음 상품의 한 줄 설명을 한 가지 옵션으로 50자 이내로 생성 후 생성 문구만 답변해주세요.";
    private final Client geminiClient;

    @Override
    public AiGenerationResult generateProductDetail(String productName) {
        String prompt = PROMPT + productName;
        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3-flash-preview",
                        prompt,
                        null
                );

        return new AiGenerationResult(
                productName,
                prompt,
                response.text()
        );
    }
}
