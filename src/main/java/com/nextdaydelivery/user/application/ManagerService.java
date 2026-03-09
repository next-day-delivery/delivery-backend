package com.nextdaydelivery.user.application;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createManager(ManagerCreateRequest request) {
        if (userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname())) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.create(
                request.username(),
                request.nickname(),
                request.email(),
                encodedPassword,
                UserRole.MANAGER,
                true
        );

        try {
            User savedUser = userRepository.save(newUser);
            return savedUser.getUserId();

        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }
    }
}
