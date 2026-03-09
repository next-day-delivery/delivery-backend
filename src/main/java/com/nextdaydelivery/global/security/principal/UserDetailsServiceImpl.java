package com.nextdaydelivery.global.security.principal;

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
        Long userId;

        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + userIdStr, e);
        }

        AuthUserDto authUserDto = userRepository.findAuthInfoById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return new PrincipalDetails(authUserDto);
    }
}
