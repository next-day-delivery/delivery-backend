package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.user.domain.entity.UserAddress;
import java.util.Optional;

public interface UserAddressRepository {

    UserAddress save(UserAddress userAddress);

    Optional<UserAddress> findActiveAddressByUserId(Long userId);
}
