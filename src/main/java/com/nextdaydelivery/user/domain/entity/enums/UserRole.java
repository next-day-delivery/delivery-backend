package com.nextdaydelivery.user.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    CUSTOMER("ROLE_CUSTOMER", "고객"),
    OWNER("ROLE_OWNER", "가게 주인"),
    MANAGER("ROLE_MANAGER", "매니저"),
    MASTER("ROLE_MASTER", "관리자");

    private final String authority;
    private final String description;
}
