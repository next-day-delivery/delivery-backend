package com.nextdaydelivery.store_category.domain.entity;

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
@Table(name = "p_store_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreCategory extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_category_id", updatable = false, nullable = false)
    private UUID storeCategoryId; // 가게-카테고리 연결 PK

    @Column(name = "store_id", nullable = false)
    private UUID storeId; // 가게 PK

    @Column(name = "category_id", nullable = false)
    private UUID categoryId; // 카테고리 PK
}
