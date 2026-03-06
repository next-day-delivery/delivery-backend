package com.nextdaydelivery.user.infrastructure;

import com.nextdaydelivery.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
}
