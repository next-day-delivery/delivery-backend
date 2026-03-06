package com.nextdaydelivery.user.domain.entity;

import com.nextdaydelivery.global.domain.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_user_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class UserAddress extends BaseAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_address_id", updatable = false, nullable = false)
    private UUID userAddressId; // 배송주소 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address", length = 255, nullable = false)
    private String address; // 배송주소 (VARCHAR 255)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @Builder
    private UserAddress(User user, String address) {
        this.user = user;
        this.address = address;
    }

    public static UserAddress create(User user, String address) {
        if (user == null) {
            throw new IllegalArgumentException("user는 필수입니다.");
        }

        if (address == null || address.isBlank() || address.length() > 255) {
            throw new IllegalArgumentException("주소는 1~255자여야 합니다.");
        }

        return UserAddress.builder()
                .user(user)
                .address(address)
                .build();
    }

    public void markAsDeleted(String deleterId) {
        if (this.deletedAt != null) {
            throw new IllegalStateException("이미 삭제된 주소입니다.");
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deleterId;
    }
}
