package com.nextdaydelivery.global.security.dto;

import com.nextdaydelivery.user.domain.entity.enums.UserRole;

public record AuthUserDto(
        Long userId,
        UserRole role
) {
}
