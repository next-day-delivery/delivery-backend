package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.annotation.RequireOwnerRole;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.user.application.UserService;
import com.nextdaydelivery.user.presentation.dto.request.AddressUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Long> publicSignup(@Valid @RequestBody PublicSignUpRequest request) {
        Long response = userService.signUp(request);
        return CommonResponse.onSuccess(HttpStatus.CREATED, response);
    }

    @PatchMapping("/me/address")
    @RequireOwnerRole
    @RequireCustomerRole
    public CommonResponse<Void> updateMyAddress(
            @Valid @RequestBody AddressUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getAuthUserDto().userId();
        userService.updateAddress(userId, request);

        return CommonResponse.onSuccess(null);
    }
}
