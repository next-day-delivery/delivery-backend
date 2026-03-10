package com.nextdaydelivery.user.application;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.AddressUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.request.CustomerSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.OwnerSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.UserProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(PublicSignUpRequest request) {
        UserRole requestedRole = request.role();
        if (!requestedRole.isPubliclyRegistrable()) {
            throw new BusinessException(UserErrorCode.INVALID_SIGNUP_ROLE);
        }

        if (userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname())) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        String resolvedAddress = switch (request) {
            case CustomerSignUpRequest c -> c.deliveryAddress();
            case OwnerSignUpRequest o -> o.businessAddress();
        };

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.create(
                request.username(),
                request.nickname(),
                request.email(),
                encodedPassword,
                request.role(),
                true
        );

        try {
            User savedUser = userRepository.save(newUser);

            UserAddress newAddress = UserAddress.create(
                    savedUser,
                    resolvedAddress
            );
            userAddressRepository.save(newAddress);

            return savedUser.getUserId();

        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }
    }

    @Transactional
    public void updateAddress(Long userId, AddressUpdateRequest request) {
        UserAddress oldAddress = userAddressRepository.findActiveAddressByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.ADDRESS_NOT_FOUND));

        if (oldAddress.getAddressText().equals(request.address())) {
            return;
        }

        oldAddress.markAsDeleted(String.valueOf(userId));

        UserAddress newAddress = UserAddress.create(oldAddress.getUser(), request.address());
        userAddressRepository.save(newAddress);
    }

    @Transactional
    public void updateMyProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!user.getNickname().equals(request.nickname()) && userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        user.updateProfile(request.nickname(), request.email());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String deleterId = String.valueOf(userId);

        user.markAsDeleted(deleterId);

        userAddressRepository.findActiveAddressByUserId(userId)
                .ifPresent(address -> address.markAsDeleted(deleterId));
    }
}
