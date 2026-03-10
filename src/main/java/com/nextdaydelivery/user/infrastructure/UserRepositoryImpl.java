package com.nextdaydelivery.user.infrastructure;

import static com.nextdaydelivery.user.domain.entity.QUser.user;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.response.ManagerResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long managerId) {
        return userJpaRepository.findById(managerId);
    }

    @Override
    public boolean existsByUniqueFields(String username, String email, String nickname) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(user)
                .where(
                        user.username.eq(username)
                                .or(user.email.eq(email))
                                .or(user.nickname.eq(nickname))
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public Optional<AuthUserDto> findAuthInfoById(Long userId) {
        AuthUserDto result = queryFactory
                .select(Projections.constructor(AuthUserDto.class,
                        user.userId,
                        user.role
                ))
                .from(user)
                .where(
                        user.userId.eq(userId),
                        user.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }


    @Override
    public Page<ManagerResponse> findManagersWithPagination(Pageable pageable) {
        List<ManagerResponse> content = queryFactory
                .select(Projections.constructor(ManagerResponse.class,
                        user.userId,
                        user.username,
                        user.email,
                        user.nickname,
                        user.role,
                        user.createdAt
                ))
                .from(user)
                .where(
                        user.role.eq(UserRole.MANAGER),
                        user.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.role.eq(UserRole.MANAGER),
                        user.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
