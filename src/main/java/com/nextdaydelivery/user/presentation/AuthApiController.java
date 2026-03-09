package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.user.application.AuthService;
import com.nextdaydelivery.user.presentation.dto.request.SignInRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public CommonResponse<String> signIn(@Valid @RequestBody SignInRequest request) {
        String accessToken = authService.signIn(request);
        return CommonResponse.onSuccess(accessToken);
    }
}
