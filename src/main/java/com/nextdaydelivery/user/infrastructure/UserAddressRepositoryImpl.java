package com.nextdaydelivery.user.infrastructure;

import static com.nextdaydelivery.user.domain.entity.QUserAddress.userAddress;

import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAddressRepositoryImpl implements UserAddressRepository {
    private final UserAddressJpaRepository userAddressJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public UserAddress save(UserAddress userAddress) {
        return userAddressJpaRepository.save(userAddress);
    }

    @Override
    public Optional<UserAddress> findActiveAddressByUserId(Long userId) {
        UserAddress result = queryFactory
                .selectFrom(userAddress)
                .where(
                        userAddress.user.userId.eq(userId),
                        userAddress.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
