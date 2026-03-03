package com.nextdaydelivery.global.baseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 테이블로 매핑되지 않고, 자식 엔티티에게 매핑 정보만 제공
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 레코드 생성 시간

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy; // 레코드 생성자

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 레코드 수정 시간

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy; // 레코드 수정자

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy; // 레코드 삭제자
}
