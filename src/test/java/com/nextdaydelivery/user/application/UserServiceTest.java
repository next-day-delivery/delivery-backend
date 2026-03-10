package com.nextdaydelivery.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.UserAddress;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserAddressRepository;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.AddressUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.request.CustomerSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("회원가입 (SignUp)")
    class SignUpTest {

        @Test
        @DisplayName("성공: 중복 없는 정상 데이터가 주어지면 비밀번호를 암호화하여 유저와 주소를 모두 저장한다.")
        void signUp_Success() {
            // given
            CustomerSignUpRequest request = new CustomerSignUpRequest(
                    "tester", "TestPassword1!", "테스터", "test@test.com", UserRole.CUSTOMER, "서울특별시 강남구"
            );

            given(userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname()))
                    .willReturn(false);

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
            assertThat(capturedAddress.getAddress()).isEqualTo(request.deliveryAddress());
        }

        @Test
        @DisplayName("실패: 이미 존재하는 아이디, 이메일, 닉네임 중 하나라도 중복되면 예외가 발생한다.")
        void signUp_Fail_DuplicateUser() {
            // given
            PublicSignUpRequest request = new CustomerSignUpRequest(
                    "duplicateUser", "TestPassword1!", "중복유저", "dup@test.com", UserRole.CUSTOMER, "서울특별시 강남구"
            );

            given(userRepository.existsByUniqueFields(request.username(), request.email(), request.nickname()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.USER_ALREADY_EXISTS.getMessage());

            then(passwordEncoder).shouldHaveNoInteractions();
            then(userRepository).should(never()).save(any());
            then(userAddressRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("주소 변경 (Update Address)")
    class UpdateAddressTest {

        @Test
        @DisplayName("성공: 새로운 주소가 입력되면 기존 주소는 만료 처리되고 새 주소가 저장된다.")
        void updateAddress_Success() {
            // given
            Long userId = 1L;
            AddressUpdateRequest request = new AddressUpdateRequest("서울특별시 서초구 신주소");

            User mockUser = User.builder().username("tester").build();
            ReflectionTestUtils.setField(mockUser, "userId", userId);

            UserAddress oldAddress = UserAddress.create(mockUser, "서울특별시 강남구 구주소");
            ReflectionTestUtils.setField(oldAddress, "userAddressId", UUID.randomUUID());

            given(userAddressRepository.findActiveAddressByUserId(userId))
                    .willReturn(Optional.of(oldAddress));

            // when
            userService.updateAddress(userId, request);

            // then
            assertThat(oldAddress.getDeletedAt()).isNotNull();
            assertThat(oldAddress.getDeletedBy()).isEqualTo(String.valueOf(userId));

            then(userAddressRepository).should(times(1)).save(addressCaptor.capture());

            UserAddress capturedNewAddress = addressCaptor.getValue();
            assertThat(capturedNewAddress.getAddress()).isEqualTo(request.address());
            assertThat(capturedNewAddress.getUser()).isEqualTo(mockUser);
        }

        @Test
        @DisplayName("성공 (최적화): 기존 주소와 100% 동일한 주소가 들어오면 저장 로직을 생략(Short-circuit)한다.")
        void updateAddress_Success_SameAddress_ShortCircuit() {
            // given
            Long userId = 1L;
            AddressUpdateRequest request = new AddressUpdateRequest("서울특별시 강남구 그대로");

            User mockUser = User.builder().username("tester").build();
            UserAddress oldAddress = UserAddress.create(mockUser, "서울특별시 강남구 그대로");

            given(userAddressRepository.findActiveAddressByUserId(userId))
                    .willReturn(Optional.of(oldAddress));

            // when
            userService.updateAddress(userId, request);

            // then
            assertThat(oldAddress.getDeletedAt()).isNull();
            then(userAddressRepository).should(never()).save(any(UserAddress.class));
        }

        @Test
        @DisplayName("실패: 활성화된 기존 주소를 찾을 수 없으면 예외가 발생한다.")
        void updateAddress_Fail_AddressNotFound() {
            // given
            Long userId = 1L;
            AddressUpdateRequest request = new AddressUpdateRequest("서울특별시 서초구 신주소");

            given(userAddressRepository.findActiveAddressByUserId(userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateAddress(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.ADDRESS_NOT_FOUND.getMessage());

            then(userAddressRepository).should(never()).save(any(UserAddress.class));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 (Delete User)")
    class DeleteUserTest {

        @Test
        @DisplayName("성공: 유저와 연결된 활성 주소가 있다면 둘 다 완벽하게 Soft Delete 처리된다.")
        void deleteUser_Success_WithAddress() {
            // given
            Long userId = 1L;
            User mockUser = User.builder().username("tester").build();
            ReflectionTestUtils.setField(mockUser, "userId", userId);

            UserAddress mockAddress = UserAddress.create(mockUser, "서울특별시 강남구");
            UUID mockAddressId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            ReflectionTestUtils.setField(mockAddress, "userAddressId", mockAddressId);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(userAddressRepository.findActiveAddressByUserId(userId))
                    .willReturn(Optional.of(mockAddress));

            // when
            userService.deleteUser(userId);

            // then
            assertThat(mockUser.getDeletedAt()).isNotNull();
            assertThat(mockUser.getDeletedBy()).isEqualTo(String.valueOf(userId));

            assertThat(mockAddress.getDeletedAt()).isNotNull();
            assertThat(mockAddress.getDeletedBy()).isEqualTo(String.valueOf(userId));

            then(userRepository).should(times(1)).findById(userId);
            then(userAddressRepository).should(times(1)).findActiveAddressByUserId(userId);
        }

        @Test
        @DisplayName("성공: 연결된 활성 주소가 없더라도 NPE 없이 유저 단독으로 정상 탈퇴된다.")
        void deleteUser_Success_WithoutAddress() {
            // given
            Long userId = 1L;
            User mockUser = User.builder().username("tester").build();
            ReflectionTestUtils.setField(mockUser, "userId", userId);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));

            given(userAddressRepository.findActiveAddressByUserId(userId))
                    .willReturn(Optional.empty());

            // when
            userService.deleteUser(userId);

            // then
            assertThat(mockUser.getDeletedAt()).isNotNull();
            assertThat(mockUser.getDeletedBy()).isEqualTo(String.valueOf(userId));

            then(userAddressRepository).should(times(1)).findActiveAddressByUserId(userId);
        }

        @Test
        @DisplayName("실패: 존재하지 않거나 이미 삭제된 유저의 식별자로 요청하면 예외가 발생한다.")
        void deleteUser_Fail_UserNotFound() {
            // given
            Long userId = 999L;

            given(userRepository.findById(userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());

            then(userAddressRepository).should(never()).findActiveAddressByUserId(any());
        }
    }
}
