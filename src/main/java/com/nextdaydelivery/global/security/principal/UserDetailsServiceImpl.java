package com.nextdaydelivery.global.security.principal;

import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long userId = Long.valueOf(userIdStr);

        AuthUserDto authUserDto = userRepository.findAuthInfoById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.UNAUTHORIZED));

        return new PrincipalDetails(authUserDto);
    }
}