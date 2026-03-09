package com.nextdaydelivery.user.infrastructure;

import com.nextdaydelivery.user.domain.entity.UserAddress;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressJpaRepository extends JpaRepository<UserAddress, UUID> {
}
