package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.user.domain.entity.UserAddress;

public interface UserAddressRepository {

    UserAddress save(UserAddress userAddress);
}
