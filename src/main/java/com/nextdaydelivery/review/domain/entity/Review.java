package com.nextdaydelivery.review.domain.entity;

import com.nextdaydelivery._domainName_sample.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewId; // 리뷰 PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK (BIGINT)

    @Column(name = "order_id", nullable = false)
    private UUID orderId; // 주문 PK

    @Column(name = "store_id", nullable = false)
    private UUID storeId; // 가게 PK

    @Column(name = "content", length = 255)
    private String content; // 리뷰 내용

    @Column(name = "rating", nullable = false)
    private Integer rating; // 별점 (INT)

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private ReviewStatus reviewStatus; // 리뷰 상태 (VISIBLE, HIDDEN)

    /**
     * 리뷰 상태 관리를 위한 ENUM
     */
    public enum ReviewStatus {
        VISIBLE, // 표시
        HIDDEN   // 숨김
    }
}
