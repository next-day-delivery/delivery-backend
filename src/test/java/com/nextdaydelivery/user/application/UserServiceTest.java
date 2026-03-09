package com.nextdaydelivery.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UserAddress> addressCaptor;

    @Test
    @DisplayName("회원가입 성공: 중복 없는 정상 데이터가 주어지면 비밀번호를 암호화하여 유저와 주소를 모두 저장한다.")
    void signUp_Success() {
        // given
        PublicSignUpRequest request = new PublicSignUpRequest(
                "tester", "TestPassword1!", "테스터", "test@test.com", UserRole.CUSTOMER, "서울특별시 강남구"
        );

        given(userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname()))
                .willReturn(false); // 중복 없음

        given(passwordEncoder.encode(request.password()))
                .willReturn("encoded_bcrypt_password_123");

        User mockSavedUser = User.builder().username(request.username()).build();
        ReflectionTestUtils.setField(mockSavedUser, "userId", 1L);

        given(userRepository.save(any(User.class)))
                .willReturn(mockSavedUser);

        // when
        Long savedUserId = userService.signUp(request);

        // then
        assertThat(savedUserId).isEqualTo(1L);

        then(userRepository).should(times(1)).save(userCaptor.capture());
        then(userAddressRepository).should(times(1)).save(addressCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(request.username());
        assertThat(capturedUser.getPassword()).isEqualTo("encoded_bcrypt_password_123");
        assertThat(capturedUser.getPassword()).isNotEqualTo(request.password());

        UserAddress capturedAddress = addressCaptor.getValue();
        assertThat(capturedAddress.getAddress()).isEqualTo(request.address());
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 아이디, 이메일, 닉네임 중 하나라도 중복되면 예외가 발생한다.")
    void signUp_Fail_DuplicateUser() {
        // given
        PublicSignUpRequest request = new PublicSignUpRequest(
                "duplicateUser", "TestPassword1!", "중복유저", "dup@test.com", UserRole.CUSTOMER, "서울특별시 강남구"
        );

        given(userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.USER_ALREADY_EXISTS.getMessage());

        then(passwordEncoder).shouldHaveNoInteractions();
        then(userRepository).should(times(0)).save(any());
        then(userAddressRepository).shouldHaveNoInteractions();
    }
}
