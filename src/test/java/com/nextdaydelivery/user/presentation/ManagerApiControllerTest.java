package com.nextdaydelivery.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.user.application.ManagerService;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import com.nextdaydelivery.user.presentation.dto.request.ManagerUpdateRequest;
import com.nextdaydelivery.user.presentation.dto.response.ManagerResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ManagerApiController.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class ManagerApiControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManagerService managerService;

    @BeforeEach
    void setUp() {
        // [인증 설정] 삭제 API 등에서 principal.getUsername()을 사용하므로 MASTER 또는 MANAGER 권한 주입
        AuthUserDto mockUser = new AuthUserDto(1L, UserRole.MASTER); // 도메인에 맞는 최고 관리자 권한 세팅
        PrincipalDetails principal = new PrincipalDetails(mockUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "master_admin_id", principal.getAuthorities())
        );
    }

    @Test
    @DisplayName("매니저 생성 API - 성공")
    void createManager() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "manager");
        requestBody.put("nickname", "최승원");
        requestBody.put("email", "choi@delivery.com");
        requestBody.put("password", "Password123!");

        given(managerService.createManager(any(ManagerCreateRequest.class))).willReturn(100L);

        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100L))
                .andDo(print())

                .andDo(document("manager-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("매니저 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("매니저 닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("매니저 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("매니저 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과").optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("생성된 매니저 식별자 (ID)")
                        )
                ));
    }

    @Test
    @DisplayName("매니저 목록 조회 API - 성공 (페이징 포함)")
    void getManagers() throws Exception {
        // given
        // 🚨 [수정 완료] 제공된 ManagerResponse의 오버로딩된 생성자 스펙에 맞게 데이터 모의(Mocking)
        ManagerResponse responseDto = new ManagerResponse(
                1L, "manager_choi", "choi@delivery.com", "최승원", UserRole.MANAGER, java.time.LocalDateTime.now()
        );
        Page<ManagerResponse> pageResponse = new PageImpl<>(List.of(responseDto), PageRequest.of(0, 10), 1);

        given(managerService.getManagers(any(Pageable.class))).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/managers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("manager-get-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").optional().description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").optional().description("페이지 크기 (기본값 10)"),
                                parameterWithName("sort").optional().description("정렬 기준 (예: id,desc)")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과").optional(),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지").optional(),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간").optional(),

                                // 🚨 [수정 완료] DTO 스펙과 100% 동기화된 Content 명세
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("매니저 데이터 목록"),
                                fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER)
                                        .description("매니저 식별자 (PK)"),
                                fieldWithPath("data.content[].username").type(JsonFieldType.STRING)
                                        .description("매니저 로그인 아이디"),
                                fieldWithPath("data.content[].email").type(JsonFieldType.STRING).description("매니저 이메일"),
                                fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING)
                                        .description("매니저 닉네임"),
                                fieldWithPath("data.content[].role").type(JsonFieldType.STRING)
                                        .description("매니저 권한 (MANAGER 등)"),
                                fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING)
                                        .description("계정 생성 일시"),

                                // Page 객체의 메타데이터 명세
                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("전체 오프셋"),
                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                        .description("비페이징 여부"),
                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                        .description("정렬 비어있음 여부"),
                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬되지 않음 여부"),

                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 비어있음 여부"),
                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                        .description("정렬되지 않음 여부"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 요소 수"),
                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어있는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("매니저 프로필 수정 API - 성공")
    void updateManagerProfile() throws Exception {
        Long managerId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nickname", "최승원");
        requestBody.put("email", "new_choi@delivery.com");

        willDoNothing().given(managerService).updateManagerProfile(eq(managerId), any(ManagerUpdateRequest.class));

        // 🚨 경로 변수 치환을 위해 RestDocumentationRequestBuilders의 patch 사용
        mockMvc.perform(patch("/api/managers/{managerId}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("manager-update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("managerId").description("수정할 매니저의 식별자 (ID)")
                        ),
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
    @DisplayName("매니저 삭제 API - 성공")
    void deleteManager() throws Exception {
        Long managerId = 1L;

        // principal에서 가져오는 deleterId는 setUp()에서 주입한 "master_admin_id"를 사용
        willDoNothing().given(managerService).deleteManager(eq(managerId), eq("master_admin_id"));

        // 🚨 경로 변수 치환을 위해 RestDocumentationRequestBuilders의 delete 사용
        mockMvc.perform(delete("/api/managers/{managerId}", managerId))
                .andExpect(status().isOk())
                .andDo(print())

                .andDo(document("manager-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("managerId").description("삭제할 매니저의 식별자 (ID)")
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
}
