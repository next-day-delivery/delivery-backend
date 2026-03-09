package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    boolean existsByUniqueFields(String username, String email, String nickname);

    Optional<User> findByUsername(String username);

    Optional<AuthUserDto> findAuthInfoById(Long userId);
}
