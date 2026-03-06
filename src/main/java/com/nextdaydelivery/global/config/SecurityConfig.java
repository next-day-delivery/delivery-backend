package com.nextdaydelivery.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 비활성화 (테스트 및 API 서버 개발 시 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()       // 메인 페이지는 누구나 접근 가능
                        .requestMatchers("/api/health").permitAll() // 나중에 쓸 헬스체크도 미리 허용
                        .anyRequest().authenticated()           // 나머지는 로그인 필요
                )

                // 3. 기본 로그인 폼 유지 (나중에 커스텀 로그인을 만들면 바꿀 부분)
                .formLogin(form -> form.defaultSuccessUrl("/"));

        return http.build();
    }
}