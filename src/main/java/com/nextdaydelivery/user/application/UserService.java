package com.nextdaydelivery.user.application;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import lombok.RequiredArgsConstructor;
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
        if (userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname())) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.create(
                request.username(),
                request.nickname(),
                request.email(),
                encodedPassword,
                request.role(),
                true
        );

        User savedUser = userRepository.save(newUser);

        UserAddress newAddress = UserAddress.create(
                savedUser,
                request.address()
        );
        userAddressRepository.save(newAddress);

        return savedUser.getUserId();
    }
}
