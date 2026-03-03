package com.nextdaydelivery.user.domain.entity.enums;

/**
 * 회원 역할을 위한 ENUM -> 추후 파일 분리
 */
public enum UserRole {
    CUSTOMER, // 일반 고객
    OWNER,    // 가게 주인
    MANAGER,  // 매니저
    MASTER    // 시스템 관리자
}
