package com.nextdaydelivery.user.presentation.dto.response;

import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.time.LocalDateTime;

public record ManagerResponse(
        Long userId,
        String username,
        String email,
        String nickname,
        String role,
        String createdAt
) {
    public static ManagerResponse from(User user) {
        return new ManagerResponse(
                user.getUserId(), user.getUsername(), user.getEmail(),
                user.getNickname(), user.getRole().name(), user.getCreatedAt().toString()
        );
    }

    public ManagerResponse(Long userId, String username, String email, String nickname, UserRole role,
                           LocalDateTime createdAt) {
        this(userId, username, email, nickname, role.name(), createdAt.toString());
    }
}
