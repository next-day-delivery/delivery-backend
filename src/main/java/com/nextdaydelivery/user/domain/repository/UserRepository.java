package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
