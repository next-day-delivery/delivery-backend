package com.nextdaydelivery.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.jwt.JwtProvider;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.SignInRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("로그인 성공: 유저 정보와 비밀번호가 일치하면 Access Token을 발급한다.")
    void signIn_Success() {
        // given
        SignInRequest request = new SignInRequest("validUser", "ValidPassword1!");
        User mockUser = User.builder()
                .username("validUser")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .build();
        ReflectionTestUtils.setField(mockUser, "userId", 1L);

        given(userRepository.findByUsername(request.username()))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(request.password(), mockUser.getPassword()))
                .willReturn(true); // 비밀번호 일치 모의
        given(jwtProvider.createAccessToken(mockUser.getUserId(), mockUser.getRole()))
                .willReturn("mocked.jwt.token");

        // when
        String accessToken = authService.signIn(request);

        // then
        assertThat(accessToken).isEqualTo("mocked.jwt.token");
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 아이디로 로그인 시도 시 예외가 발생하며, Bcrypt 연산은 수행되지 않는다.")
    void signIn_Fail_UserNotFound() {
        // given
        SignInRequest request = new SignInRequest("invalidUser", "Password123!");

        given(userRepository.findByUsername(request.username()))
                .willReturn(Optional.empty()); // DB에 유저 없음

        // when & then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthErrorCode.INVALID_CREDENTIALS.getMessage());

        then(passwordEncoder).shouldHaveNoInteractions();
        then(jwtProvider).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호가 불일치할 경우 예외가 발생하며, JWT 토큰은 발급되지 않는다.")
    void signIn_Fail_InvalidPassword() {
        // given
        SignInRequest request = new SignInRequest("validUser", "WrongPassword!");
        User mockUser = User.builder()
                .username("validUser")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .build();

        given(userRepository.findByUsername(request.username()))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(request.password(), mockUser.getPassword()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthErrorCode.INVALID_CREDENTIALS.getMessage());

        then(jwtProvider).shouldHaveNoInteractions();
    }
}
