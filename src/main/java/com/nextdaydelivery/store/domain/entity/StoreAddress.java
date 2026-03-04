package com.nextdaydelivery.store.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_address_id", updatable = false, nullable = false)
    private Long storeAddressId; // 가게 주소 PK (BIGINT)

    @Column(name = "sigungu", length = 32)
    private String sigungu; // 시군구명 (VARCHAR 32)

    @Column(name = "sido", length = 32)
    private String sido; // 시도명 (VARCHAR 32)

    @Column(name = "dong", length = 32)
    private String dong; // 읍면동명 (VARCHAR 32)
}
