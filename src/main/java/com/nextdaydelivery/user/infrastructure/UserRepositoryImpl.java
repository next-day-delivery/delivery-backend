package com.nextdaydelivery.user.infrastructure;

import static com.nextdaydelivery.user.domain.entity.QUser.user;

import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
