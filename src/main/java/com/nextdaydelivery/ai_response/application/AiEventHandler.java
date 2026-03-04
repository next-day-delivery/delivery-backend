package com.nextdaydelivery.ai_response.application;

import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.ai_response.domain.entity.AiResponse;
import com.nextdaydelivery.ai_response.domain.repository.AiResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiEventHandler {

    private final AiResponseRepository aiResponseRepository;

    @EventListener
    @Transactional
    public void handleAiUsedEvent(AiUsedEvent event) {
        AiResponse aiResponse = AiResponse.fromEvent(event);
        aiResponseRepository.save(aiResponse);
    }
}
