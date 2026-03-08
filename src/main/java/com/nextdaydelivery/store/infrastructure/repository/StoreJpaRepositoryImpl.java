package com.nextdaydelivery.store.infrastructure.repository;

import com.nextdaydelivery.store.domain.entity.QStore;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.repository.StoreQuerydslRepository;
import com.nextdaydelivery.store.presentation.dto.StoreSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class StoreJpaRepositoryImpl implements StoreQuerydslRepository {

    private final JPAQueryFactory queryFactory; // Config에서 등록한 빈이 주입됨
    private final QStore store = QStore.store;   // QueryDSL이 생성한 Q클래스

    @Override
    public Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable) {
        List<Store> content = queryFactory
                .selectFrom(store)
                .leftJoin(store.storeAddress).fetchJoin()
                .where(
                        nameContains(condition.name()),
                        store.deletedAt.isNull() // 삭제된 가게 제외 로직 추가!
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 필요 시 카운트 쿼리도 실행
        JPAQuery<Long> countQuery = queryFactory
                .select(store.count())
                .from(store)
                .where(
                        nameContains(condition.name()),
                        store.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? store.name.contains(name) : null;
    }
}
