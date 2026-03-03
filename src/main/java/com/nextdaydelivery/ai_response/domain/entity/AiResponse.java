package com.nextdaydelivery.ai_response.domain.entity;

import com.nextdaydelivery._domainName_sample.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_ai_response")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiResponse extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "res_id", updatable = false, nullable = false)
    private UUID resId; // 응답PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK (BIGINT)

    @Column(name = "product_name", length = 100)
    private String productName; // 상품명

    @Column(name = "prompt", length = 255)
    private String prompt; // 질문

    @Column(name = "content", length = 255)
    private String content; // 답변
}