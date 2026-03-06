package com.nextdaydelivery.global.security.jwt;

import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, UserRole role) {
        Objects.requireNonNull(userId, "User ID는 null일 수 없습니다.");
        Objects.requireNonNull(role, "User Role은 null일 수 없습니다.");

        Date now = new Date();

        long expirationMillis = jwtProperties.accessTokenValidity().toMillis();
        Date validity = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }
}
