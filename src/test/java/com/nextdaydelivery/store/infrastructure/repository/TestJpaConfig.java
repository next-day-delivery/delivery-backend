package com.nextdaydelivery.store.infrastructure.repository;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
// 메인 설정의 빈 이름(customAuditorAware)과 맞춰줍니다.
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
public class TestJpaConfig {

    @Bean
    public AuditorAware<String> customAuditorAware() {
        // 테스트 환경에서 기록될 생성자/수정자 이름입니다.
        return () -> Optional.of("TEST_AUDITOR");
    }
}