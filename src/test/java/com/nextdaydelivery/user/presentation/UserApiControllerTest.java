package com.nextdaydelivery.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.user.application.UserService;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.presentation.dto.request.AddressUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.request.PublicSignUpRequest;
import com.nextdaydelivery.user.presentation.dto.request.UserProfileUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {UserApiController.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class UserApiControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        AuthUserDto mockUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        PrincipalDetails principal = new PrincipalDetails(mockUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities())
        );
    }

    @Test
    @DisplayName("회원가입 API - 성공")
    void publicSignup() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "tester123");
        requestBody.put("password", "Password123!");
        requestBody.put("nickname", "미르미");
        requestBody.put("email", "test@test.com");
        requestBody.put("role", "CUSTOMER");
        requestBody.put("deliveryAddress", "서울특별시 강남구");

        given(userService.signUp(any(PublicSignUpRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(1L))
                .andDo(print())

                .andDo(document("user-public-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한 (CUSTOMER 또는 OWNER)"),
                                fieldWithPath("deliveryAddress").type(JsonFieldType.STRING).optional()
                                        .description("배송지 주소 (CUSTOMER 시 필수)"),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).optional()
                                        .description("사업자 번호 (OWNER 시 필수)")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)")
                                        .optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("생성된 유저 식별자 번호 (PK)")
                        )
                ));
    }

    @Test
    @DisplayName("내 주소 변경 API - 성공")
    void updateMyAddress() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest("서울특별시 서초구 반포동 123");

        willDoNothing().given(userService).updateAddress(eq(1L), any(AddressUpdateRequest.class));

        mockMvc.perform(patch("/api/users/me/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("user-update-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("address").type(JsonFieldType.STRING).description("변경할 새로운 주소 텍스트")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과").optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("내 정보 수정 API - 성공")
    void updateMyProfile() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("새로운닉네임", "new@test.com");

        willDoNothing().given(userService).updateMyProfile(eq(1L), any(UserProfileUpdateRequest.class));

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("user-update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("변경할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과").optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 API - 성공")
    void deleteUser() throws Exception {
        willDoNothing().given(userService).deleteUser(eq(1L));

        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("user-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과").optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)").optional()
                        )
                ));
    }
}
