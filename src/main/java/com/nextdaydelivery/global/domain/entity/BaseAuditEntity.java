package com.nextdaydelivery.global.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public abstract class BaseAuditEntity extends CreatedAuditEntity {

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 레코드 수정 시간

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy; // 레코드 수정자
}
