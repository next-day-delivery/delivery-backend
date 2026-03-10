package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.user.application.UserService;
import com.nextdaydelivery.user.presentation.dto.request.AddressUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    public CommonResponse<Void> updateMyAddress(
            @Valid @RequestBody AddressUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getAuthUserDto().userId();
        userService.updateAddress(userId, request);

        return CommonResponse.onSuccess(null);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    public CommonResponse<Void> updateMyProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getAuthUserDto().userId();

        userService.updateMyProfile(userId, request);

        return CommonResponse.onSuccess(null);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    public CommonResponse<Void> deleteUser(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getAuthUserDto().userId();

        userService.deleteUser(userId);

        return CommonResponse.onSuccess(null);
    }
}
