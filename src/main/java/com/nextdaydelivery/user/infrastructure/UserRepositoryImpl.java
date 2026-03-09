package com.nextdaydelivery.user.infrastructure;

import static com.nextdaydelivery.user.domain.entity.QUser.user;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.user.domain.entity.QUser;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
        QUser user = QUser.user;

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
}
