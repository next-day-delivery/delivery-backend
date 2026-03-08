package com.nextdaydelivery.ai_response.application.event;

import com.nextdaydelivery.ai_response.infrastructure.dto.AiGenerationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AiUsedEvent {

    private String productName;

    private String prompt;

    private String content;

    public static AiUsedEvent from(AiGenerationResult result) {
        return AiUsedEvent.builder()
                .productName(result.productName())
                .prompt(result.prompt())
                .content(result.content())
                .build();
    }
}
