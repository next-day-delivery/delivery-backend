package com.nextdaydelivery.user.domain.entity;

import com.nextdaydelivery.global.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId; // 회원 PK (BIGINT)

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username; // 회원 ID

    @Column(name = "nickname", length = 100, nullable = false)
    private String nickname; // 닉네임

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email; // 이메일

    @Column(name = "password", length = 255, nullable = false)
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role; // 역할 (CUSTOMER, OWNER, MANAGER, MASTER)

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic; // 공개 여부 (BOOLEAN)

    /**
     * 회원 역할을 위한 ENUM -> 추후 파일 분리
     */
    public enum UserRole {
        CUSTOMER, // 일반 고객
        OWNER,    // 가게 주인
        MANAGER,  // 매니저
        MASTER    // 시스템 관리자
    }
}
