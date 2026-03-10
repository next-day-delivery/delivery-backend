package com.nextdaydelivery.global.config;

import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
@Profile("!test")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> customAuditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.of("ANONYMOUS");
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof PrincipalDetails principalDetails) {
                return Optional.of(principalDetails.getUsername());
            }

            return Optional.of("SYSTEM");
        };
    }
}
