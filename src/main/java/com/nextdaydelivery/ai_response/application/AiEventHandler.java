package com.nextdaydelivery.ai_response.application;

import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.ai_response.domain.entity.AiResponse;
import com.nextdaydelivery.ai_response.domain.repository.AiResponseRepository;
import com.nextdaydelivery.global.domain.error.GlobalErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiEventHandler {

    private final AiResponseRepository aiResponseRepository;

    @Async("aiEventExecutor")
    @EventListener
    @Transactional
    public void handleAiUsedEvent(AiUsedEvent event) {
        try {
            AiResponse aiResponse = AiResponse.fromEvent(event);
            aiResponseRepository.save(aiResponse);
        } catch (Exception e) {
            log.error("AI 응답 저장 실패 (시스템 오류) - productName={}", event.getProductName(), e);
            throw new BusinessException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
