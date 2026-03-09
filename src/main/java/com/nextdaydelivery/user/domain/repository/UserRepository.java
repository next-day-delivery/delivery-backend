package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.presentation.dto.response.ManagerResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save(User user);

    boolean existsByUniqueFields(String username, String email, String nickname);

    Optional<User> findByUsername(String username);

    Optional<AuthUserDto> findAuthInfoById(Long userId);

    Page<ManagerResponse> findManagersWithPagination(Pageable pageable);
}
