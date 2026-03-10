package com.nextdaydelivery.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.nextdaydelivery.global.domain.error.UserErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import com.nextdaydelivery.user.presentation.dto.request.ManagerUpdateRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ManagerService managerService;

    private User createMockUser(Long id, UserRole role) {
        User user = User.create("testManager", "매니저", "test@test.com", "encodedPassword", role, true);
        ReflectionTestUtils.setField(user, "userId", id);
        return user;
    }

    @Nested
    @DisplayName("매니저 생성 (createManager)")
    class CreateManagerTest {

        @Test
        @DisplayName("성공: 모든 검증을 통과하고 매니저가 생성된다.")
        void success() {
            // given
            ManagerCreateRequest request = new ManagerCreateRequest("testManager", "매니저", "test@test.com",
                    "rawPassword");
            given(userRepository.existsByUniqueFields(anyString(), anyString(), anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

            User savedUser = createMockUser(1L, UserRole.MANAGER);
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            Long resultId = managerService.createManager(request);

            // then
            assertThat(resultId).isEqualTo(1L);
            then(userRepository).should(times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 필드(아이디/이메일/닉네임)가 있으면 예외가 발생한다.")
        void fail_AlreadyExists_PreCheck() {
            // given
            ManagerCreateRequest request = new ManagerCreateRequest("testManager", "매니저", "test@test.com",
                    "rawPassword");
            given(userRepository.existsByUniqueFields(anyString(), anyString(), anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> managerService.createManager(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.USER_ALREADY_EXISTS.getMessage());

            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 동시성 이슈로 save 시점에 DB 제약조건 위반 시 예외가 발생한다.")
        void fail_AlreadyExists_RaceCondition() {
            // given
            ManagerCreateRequest request = new ManagerCreateRequest("testManager", "매니저", "test@test.com",
                    "rawPassword");
            given(userRepository.existsByUniqueFields(anyString(), anyString(), anyString())).willReturn(
                    false);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

            given(userRepository.save(any(User.class))).willThrow(DataIntegrityViolationException.class);

            // when & then
            assertThatThrownBy(() -> managerService.createManager(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.USER_ALREADY_EXISTS.getMessage());
        }
    }

    @Nested
    @DisplayName("매니저 정보 수정 (updateManagerProfile)")
    class UpdateManagerProfileTest {

        @Test
        @DisplayName("성공: 닉네임과 이메일이 성공적으로 변경된다.")
        void success() {
            // given
            User manager = createMockUser(1L, UserRole.MANAGER);
            ManagerUpdateRequest request = new ManagerUpdateRequest("새로운닉네임", "new@test.com");

            given(userRepository.findById(1L)).willReturn(Optional.of(manager));
            given(userRepository.existsByNickname("새로운닉네임")).willReturn(false);
            given(userRepository.existsByEmail("new@test.com")).willReturn(false);

            // when
            managerService.updateManagerProfile(1L, request);

            assertThat(manager.getNickname()).isEqualTo("새로운닉네임");
            assertThat(manager.getEmail()).isEqualTo("new@test.com");
        }
    }

    @Nested
    @DisplayName("매니저 삭제 (deleteManager)")
    class DeleteManagerTest {

        @Test
        @DisplayName("성공: MANAGER 권한의 유저는 Soft Delete 처리된다.")
        void success() {
            // given
            User manager = createMockUser(1L, UserRole.MANAGER);
            given(userRepository.findById(1L)).willReturn(Optional.of(manager));

            // when
            managerService.deleteManager(1L, "MASTER_1");

            // then
            assertThat(manager.getDeletedAt()).isNotNull();
            assertThat(manager.getDeletedBy()).isEqualTo("MASTER_1");
        }

        @Test
        @DisplayName("실패: MASTER 권한의 유저를 삭제하려 하면 예외가 발생한다.")
        void fail_DeleteMaster() {
            // given
            User master = createMockUser(1L, UserRole.MASTER);
            given(userRepository.findById(1L)).willReturn(Optional.of(master));

            // when & then
            assertThatThrownBy(() -> managerService.deleteManager(1L, "MASTER_1"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserErrorCode.INVALID_ROLE_OPERATION.getMessage());

            assertThat(master.getDeletedAt()).isNull();
        }
    }
}
