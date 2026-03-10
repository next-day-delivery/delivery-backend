package com.nextdaydelivery.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
        @NotBlank(message = "변경할 주소를 입력해주세요.")
        @Size(max = 255)
        String address
) {
}
