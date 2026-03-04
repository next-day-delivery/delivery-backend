package com.nextdaydelivery.ai_response.application;

import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;

public interface AiEventPublisher {

    void publishEvent(AiUsedEvent event);
}
