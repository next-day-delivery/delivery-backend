package com.nextdaydelivery.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressUpdateRequest(
        @NotBlank(message = "변경할 주소를 입력해주세요.")
        String address
) {
}
