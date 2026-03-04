package com.nextdaydelivery.ai_response.domain.entity;

import com.nextdaydelivery.ai_response.application.event.AiUsedEvent;
import com.nextdaydelivery.global.domain.CreatedAuditEntity;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_ai_response")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiResponse extends CreatedAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "res_id", updatable = false, nullable = false)
    private UUID resId; // 응답PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_name", length = 100)
    private String productName; // 상품명

    @Column(name = "prompt", length = 255)
    private String prompt; // 질문

    @Column(name = "content", length = 255)
    private String content; // 답변

    @Builder
    public AiResponse(User user, String productName, String prompt, String content) {
        this.user = user;
        this.productName = productName;
        this.prompt = prompt;
        this.content = content;
    }

    public static AiResponse fromEvent(AiUsedEvent event) {
        return AiResponse.builder()
                .user(null) // TODO: user 개발 후 추가 로직 필요
                .productName(event.getProductName())
                .prompt(event.getPrompt())
                .content(event.getContent())
                .build();
    }
}
