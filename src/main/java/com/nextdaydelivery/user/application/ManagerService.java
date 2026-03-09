package com.nextdaydelivery.user.application;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import com.nextdaydelivery.user.presentation.dto.request.ManagerUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.response.ManagerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public Page<ManagerResponse> getManagers(Pageable pageable) {
        return userRepository.findManagersWithPagination(pageable);
    }

    @Transactional
    public void updateManagerProfile(Long managerId, ManagerUpdateRequest request) {
        User manager = getActiveManager(managerId);

        if (!manager.getNickname().equals(request.nickname()) && userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (!manager.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        manager.updateProfile(request.nickname(), request.email());
    }

    private User getActiveManager(Long managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.MANAGER) {
            throw new BusinessException(UserErrorCode.INVALID_ROLE_OPERATION);
        }
        if (user.getDeletedAt() != null) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_DELETED);
        }
        return user;
    }

    @Transactional
    public void deleteManager(Long managerId, String deleterId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (manager.getRole() == UserRole.MASTER) {
            throw new BusinessException(UserErrorCode.INVALID_ROLE_OPERATION);
        }

        manager.markAsDeleted(deleterId);
    }
}
