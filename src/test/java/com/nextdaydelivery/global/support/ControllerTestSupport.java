package com.nextdaydelivery.global.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.global.security.jwt.JwtValidator;
import com.nextdaydelivery.product.presentation.ProductController;
import com.nextdaydelivery.review.presentation.controller.ReviewControllerImplTest;
import com.nextdaydelivery.store.presentation.controller.StoreControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
        ProductController.class,
        StoreControllerTest.class,
        ReviewControllerImplTest.class
        // TODO: 컨트롤러 테스트를 추가할 때마다 여기에 명시.
})
public abstract class ControllerTestSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtValidator jwtValidator;
}
