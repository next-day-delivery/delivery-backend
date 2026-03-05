package com.nextdaydelivery.user.domain.repository;

import com.nextdaydelivery.user.domain.entity.User;

public interface UserRepository {

    User save(User user);

    boolean existsByUniqueFields(String username, String email, String nickname);
}
