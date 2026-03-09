package com.nextdaydelivery.product.domain.repository;

import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private static final QProduct product = QProduct.product;
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Product> searchByConditions(String name,
                                             Integer minPrice,
                                             Integer maxPrice,
                                             UUID cursorId,
                                             Pageable pageable) {

        int pageSize = pageable.getPageSize();

        // 조회 조건
        BooleanExpression predicate = nameCondition(name)
                .and(priceCondition(minPrice, maxPrice))
                .and(cursorCondition(cursorId));

        // Querydsl 조회: createdAt DESC, productId DESC
        List<Product> contents = queryFactory
                .selectFrom(product)
                .where(predicate)
                .orderBy(product.createdAt.desc(), product.productId.desc())
                .limit(pageSize + 1)  // hasNext 판단
                .fetch();

        boolean hasNext = contents.size() > pageSize;
        if (hasNext) {
            contents.remove(pageSize);
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression nameCondition(String name) {
        return name == null ? null : product.productName.containsIgnoreCase(name);
    }

    private BooleanExpression priceCondition(Integer minPrice, Integer maxPrice) {
        BooleanExpression predicate = null;
        if (minPrice != null) {
            predicate = product.price.goe(minPrice);
        }
        if (maxPrice != null) {
            predicate = predicate == null
                    ? product.price.loe(maxPrice)
                    : predicate.and(product.price.loe(maxPrice));
        }
        return predicate;
    }

    /**
     * cursorId 기준으로 커서 페이징 조회된 결과 중 마지막 productId(cursor)보다 작은 데이터를 가져옵니다. createdAt 기준 DESC 정렬 + UUID DESC tie-break
     */
    private BooleanExpression cursorCondition(UUID cursorId) {
        if (cursorId == null) {
            return null;
        }

        // 커서 Product의 createdAt을 내부적으로 가져오기 위해 서브쿼리
        // 간단히 Querydsl에서 join 없이 처리 가능
        Product cursorProduct = queryFactory
                .selectFrom(product)
                .where(product.productId.eq(cursorId))
                .fetchOne();

        if (cursorProduct == null) {
            return null;
        }

        return product.createdAt.lt(cursorProduct.getCreatedAt())
                .or(
                        product.createdAt.eq(cursorProduct.getCreatedAt())
                                .and(product.productId.lt(cursorId))
                );
    }
}