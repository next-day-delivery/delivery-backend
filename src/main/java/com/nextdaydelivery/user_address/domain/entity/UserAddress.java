package com.nextdaydelivery.user_address.domain.entity;

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
@Table(name = "p_user_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_address_id", updatable = false, nullable = false)
    private UUID userAddressId; // 배송주소 PK

    @Column(name = "user_id", nullable = false)
    private Long userId; // 회원 PK (BIGINT)

    @Column(name = "address", length = 255, nullable = false)
    private String address; // 배송주소 (VARCHAR 255)
}
