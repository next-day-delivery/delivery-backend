package com.nextdaydelivery.user.presentation.dto.request;

import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OwnerSignUpRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4~10자의 영문 소문자와 숫자만 가능합니다.")
        String username,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
                message = "비밀번호는 8~15자, 대/소문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotNull(message = "가입 유형(CUSTOMER, OWNER)을 선택해주세요.")
        UserRole role,

        @NotBlank(message = "사업자 주소는 필수 입력값입니다.")
        @Size(max = 255)
        String businessAddress
) implements PublicSignUpRequest {

    public OwnerSignUpRequest {
        if (role != UserRole.OWNER) {
            throw new IllegalArgumentException("OWNER 가입 요청의 권한은 OWNER여야 합니다.");
        }
    }
}
