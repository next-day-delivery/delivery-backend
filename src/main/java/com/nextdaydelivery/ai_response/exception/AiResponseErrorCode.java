package com.nextdaydelivery.ai_response.exception;

import com.nextdaydelivery.global.domain.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiResponseErrorCode implements ErrorCode {

    AI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "A001", "요청된 API의 응답 결과가 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
