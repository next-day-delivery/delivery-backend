package com.nextdaydelivery.review.domain.entity;

import com.nextdaydelivery.global.domain.entity.CreatedAuditEntity;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Review extends CreatedAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewId; // 리뷰 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Review 엔티티 내부
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order; // 주문 완료 or 배달 완료 상태에서 리뷰 작성 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 가게 PK

    @Column(name = "content", length = 255)
    private String content; // 리뷰 내용

    @Column(name = "rating")
    private Integer rating; // 별점 (INT)

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private ReviewStatus reviewStatus; // 리뷰 상태 (VISIBLE, HIDDEN)

    public void toggleStatus() {
        this.reviewStatus = (this.reviewStatus == ReviewStatus.VISIBLE)
            ? ReviewStatus.HIDDEN
            : ReviewStatus.VISIBLE;
    }

    public void updateReview(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}
