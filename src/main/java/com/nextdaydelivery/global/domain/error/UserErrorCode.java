package com.nextdaydelivery.global.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 존재하는 사용자입니다."),
    INVALID_SIGNUP_ROLE(HttpStatus.BAD_REQUEST, "U002", "허용되지 않은 가입 권한입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "사용자를 찾을 수 없습니다."),
    INVALID_ROLE_OPERATION(HttpStatus.BAD_REQUEST, "U004", "해당 역할로 수행할 수 없는 작업입니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "U005", "이미 삭제된 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U006", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U007", "이미 존재하는 이메일입니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "U008", "사용자 주소를 찾을 수 없습니다."),
    DUPLICATE_PROFILE_INFO(HttpStatus.CONFLICT, "U009", "이미 존재하는 프로필 정보입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
