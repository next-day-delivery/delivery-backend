package com.nextdaydelivery.global.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.global.security.jwt.JwtValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class ControllerTestSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtValidator jwtValidator;
}
