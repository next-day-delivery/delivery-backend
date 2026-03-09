package com.nextdaydelivery.user.infrastructure;

import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAddressRepositoryImpl implements UserAddressRepository {
    private final UserAddressJpaRepository userAddressJpaRepository;

    @Override
    public UserAddress save(UserAddress userAddress) {
        return userAddressJpaRepository.save(userAddress);
    }
}
