package com.nextdaydelivery.global.security.resolver;

import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.exception.BusinessException;
import com.nextdaydelivery.global.security.annotation.AuthUser;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(AuthUser.class);
        boolean isAuthUserDtoType = AuthUserDto.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && isAuthUserDtoType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserDto authUserDto)) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        return authUserDto;
    }
}
