package com.nextdaydelivery.product.domain.entity;

import com.nextdaydelivery.product.application.dto.request.ProductCreateRequest;
import com.nextdaydelivery.global.domain.entity.BaseAuditEntity;
import com.nextdaydelivery.store.domain.entity.Store;
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

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseAuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId; // 상품 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "product_detail", length = 255)
    private String productDetail;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 레코드 삭제 시간

    @Column(name = "deleted_by", length = 100)
    private String deletedBy; // 레코드 삭제자

    @Builder
    public Product(Store store, String productName, Integer price, String productDetail, boolean isHidden) {
        this.store = store;
        this.productName = productName;
        this.price = price;
        this.productDetail = productDetail;
        this.isHidden = isHidden;
    }

    public static Product ofCreateRequest(ProductCreateRequest createRequest,
                                          String productDetail) {
        return Product.builder()
//                .store(createRequest.storeId())
                .store(null) // TODO : store 개발 후 추가 로직 필요
                .productName(createRequest.productName())
                .productDetail(productDetail)
                .price(createRequest.price())
                .build();
    }

    public void updatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 양수여야 합니다.");
        }

        this.price = price;
    }

    public void updateProductName(String productName) {
        this.productName = productName;
    }

    public void updateProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public void hide() {
        this.isHidden = true;
    }
}