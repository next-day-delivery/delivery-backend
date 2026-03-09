package com.nextdaydelivery.ai_response.infrastructure;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.nextdaydelivery.ai_response.application.AiClient;
import com.nextdaydelivery.ai_response.exception.AiResponseErrorCode;
import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import com.nextdaydelivery.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiAiClient implements AiClient {

    private static final String PROMPT = "고객의 관심을 이끌 수 있도록 다음 상품의 한 줄 설명을 한 가지 옵션으로 50자 이내로 생성 후 생성 문구만 답변해주세요.";
    private static final int MINIMUM_LENGTH = 0;
    private static final int MAXIMUM_LENGTH = 255;

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

        validateResponse(response);

        String content = normalizeContent(response.text());

        return new AiGenerationResult(
                productName,
                prompt,
                content
        );
    }

    private String normalizeContent(String content) {
        String rawContent = content == null ? "" : content.trim();

        return rawContent.length() > MAXIMUM_LENGTH
                ? rawContent.substring(MINIMUM_LENGTH, MAXIMUM_LENGTH)
                : rawContent;
    }

    private void validateResponse(GenerateContentResponse result) {
        if (result == null) {
            throw new BusinessException(AiResponseErrorCode.AI_RESPONSE_EMPTY);
        }
    }
}
