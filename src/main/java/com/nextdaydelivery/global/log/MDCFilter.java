package com.nextdaydelivery.global.log;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final int TRACE_ID_MAX_LEN = 64;
    private static final String TRACE_ID_PATTERN = "^[A-Za-z0-9._-]{1,64}$";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // HTTP 요청 헤더에 트레이스 ID가 없다면 새로 생성
        String traceId = ((HttpServletRequest) request).getHeader("X-Request-ID");
        if (traceId == null
                || traceId.isBlank()
                || traceId.length() > TRACE_ID_MAX_LEN
                || !traceId.matches(TRACE_ID_PATTERN)) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }

        // ThreadLocal에 Trace ID 바인딩
        MDC.put(TRACE_ID, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}