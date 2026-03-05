package com.nextdaydelivery.store.domain.entity;

import com.nextdaydelivery.global.domain.entity.BaseAuditEntity;
import com.nextdaydelivery.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId; // 가게 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_address_id", nullable = false)
    private StoreAddress storeAddress; // 가게 위치 정보 (p_store_address 참조)

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 가게명

    @Column(name = "rating_avg", precision = 2, scale = 1)
    private BigDecimal ratingAvg; // 가게 평점 (DECIMAL 2,1)

    @Column(name = "review_count")
    private Integer reviewCount; // 리뷰 수 (INT)

    @Column(name = "detail_address", length = 255)
    private String detailAddress; // 상세 주소

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy; // 레코드 삭제자
}
