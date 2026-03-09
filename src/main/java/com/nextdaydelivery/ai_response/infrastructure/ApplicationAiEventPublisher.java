package com.nextdaydelivery.ai_response.infrastructure;

import com.nextdaydelivery.ai_response.application.AiEventPublisher;
import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationAiEventPublisher implements AiEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishEvent(AiUsedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
