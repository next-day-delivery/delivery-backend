package com.nextdaydelivery.user.infrastructure;

import com.nextdaydelivery.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
