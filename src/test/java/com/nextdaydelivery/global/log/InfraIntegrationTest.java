package com.nextdaydelivery.global.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = InfraIntegrationTest.DummyController.class)
@AutoConfigureMockMvc
@Import({MDCFilter.class, LogAspect.class})
@WithMockUser
class InfraIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class DummyController {
        @GetMapping("/dummy/success")
        public String success() {
            return "OK";
        }

        @GetMapping("/dummy/fail")
        public String fail() {
            throw new IllegalArgumentException("의도된 비즈니스 예외");
        }
    }

    @Test
    @DisplayName("MDCFilter: 요청이 끝난 후 ThreadLocal의 Trace ID가 완벽하게 삭제되어야 한다")
    void mdcFilter_clears_traceId_after_request() throws Exception {
        // Given & When
        mockMvc.perform(get("/dummy/success")
                        .header("X-Request-ID", "CUSTOM-ID-1234"))
                .andExpect(status().isOk());

        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    @DisplayName("LogAspect: 타겟 메서드에서 예외 발생 시, AOP가 예외를 은폐하지 않고 밖으로 전파해야 한다")
    void logAspect_must_rethrow_exceptions() {
        // Given & When & Then:
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mockMvc.perform(get("/dummy/fail")))
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("의도된 비즈니스 예외");
    }
}
