package com.nextdaydelivery.global.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
@Profile("!test")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> customAuditorAware() {
        return () -> {
            // TODO: 추후 JWT 도입 시 SecurityContextHolder에서 유저 ID 추출 로직으로 교체
            return Optional.of("SYSTEM_USER");
        };
    }
}
