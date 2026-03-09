package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.user.application.UserService;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public CommonResponse<Long> publicSignup(
            @Valid @RequestBody PublicSignUpRequest request
    ) {
        Long response = userService.signUp(request);
        return CommonResponse.onSuccess(HttpStatus.CREATED, response);
    }
}
