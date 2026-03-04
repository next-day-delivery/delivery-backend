package com.nextdaydelivery.user.domain.entity;

import com.nextdaydelivery.global.domain.BaseAuditEntity;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
public class User extends BaseAuditEntity {

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
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role; // 역할 (CUSTOMER, OWNER, MANAGER, MASTER)

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic; // 공개 여부 (BOOLEAN)
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy; // 레코드 삭제자
}
