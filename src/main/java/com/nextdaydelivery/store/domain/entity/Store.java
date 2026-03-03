package com.nextdaydelivery.store.domain.entity;

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
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId; // 가게 PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK (Owner)

    @Column(name = "store_address_id", nullable = false)
    private Long storeAddressId; // 가게 주소 PK

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 가게명

    @Column(name = "rating_avg")
    private Double ratingAvg; // 가게 평점 (DECIMAL 2,1)

    @Column(name = "review_count")
    private Integer reviewCount; // 리뷰 수 (INT)

    @Column(name = "detail_address", length = 255)
    private String detailAddress; // 상세 주소
}
