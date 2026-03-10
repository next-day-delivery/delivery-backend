package com.nextdaydelivery.product.domain.repository;

import com.nextdaydelivery.product.domain.entity.Product;
import com.nextdaydelivery.product.domain.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private static final QProduct product = QProduct.product;
    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;


    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }

    @Override
    public Slice<Product> searchByConditions(String name,
                                             Integer minPrice,
                                             Integer maxPrice,
                                             UUID cursorId,
                                             UUID storeId,
                                             Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<Product> contents = queryFactory
                .selectFrom(product)
                .where(
                        nameCondition(name),
                        priceCondition(minPrice, maxPrice),
                        cursorCondition(cursorId),
                        storeCondition(storeId),
                        product.deletedAt.isNull()
                )
                .orderBy(product.createdAt.desc(), product.productId.desc())
                .limit(pageSize + 1)
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

    private BooleanExpression cursorCondition(UUID cursorId) {
        if (cursorId == null) {
            return null;
        }

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

    private BooleanExpression storeCondition(UUID storeId) {
        return storeId == null ? null : product.store.storeId.eq(storeId);
    }
}
