package com.nextdaydelivery.review.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import com.nextdaydelivery.global.support.ControllerTestSupport;
import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class ReviewControllerImplTest extends ControllerTestSupport {

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        PrincipalDetails principal = new PrincipalDetails(authUser);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities())
        );
    }

    @Test
    void 가게_리뷰_전체_조회() throws Exception {
        //given
        UUID storeId = UUID.randomUUID();
        ReviewList reviewList = new ReviewList(UUID.randomUUID(), "맛있어요", 5, ReviewStatus.VISIBLE);
        Page<ReviewList> reviewPage = new PageImpl<>(List.of(reviewList), PageRequest.of(0, 10), 1);

        given(reviewService.getReview(eq(storeId), any())).willReturn(reviewPage);

        //when & then
        mockMvc.perform(
                get("/api/reviews/{storeId}", storeId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].content").value("맛있어요"))
            .andExpect(jsonPath("$.data.content[0].rating").value(5))
            .andExpect(jsonPath("$.data.content[0].reviewStatus").value("VISIBLE"))
            .andDo(print())
            .andDo(document("review-get-by-store",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("storeId").description("가게 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data.content[].reviewId").type(JsonFieldType.STRING).description("리뷰 ID"),
                    fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("별점 (1~5)"),
                    fieldWithPath("data.content[].reviewStatus").type(JsonFieldType.STRING).description("리뷰 상태 (VISIBLE / HIDDEN)"),
                    fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                    fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                    fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                    fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                    fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                    fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                    fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("미페이징 여부"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                    fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                    fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                    fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                    fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                    fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                    fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("빈 페이지 여부")
                )
            ));
    }

    @Test
    void 내_리뷰_저장() throws Exception {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest("맛없어요", 1);
        UUID orderId = UUID.randomUUID();
        Review review = Review.of(request.content(), request.rating(), ReviewStatus.VISIBLE);

        given(reviewService.saveReview(any(), eq(orderId), any())).willReturn(review);

        //when & then
        mockMvc.perform(
                post("/api/reviews/write/{orderId}", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value("맛없어요"))
            .andExpect(jsonPath("$.data.rating").value(1))
            .andExpect(jsonPath("$.data.message").value("리뷰가 저장되었습니다."))
            .andDo(print())
            .andDo(document("review-save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("orderId").description("주문 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점 (1~5)")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("저장된 리뷰 내용"),
                    fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("저장된 별점"),
                    fieldWithPath("data.message").type(JsonFieldType.STRING).description("처리 결과 메시지")
                )
            ));
    }

    @Test
    void 내_리뷰_전체_조회() throws Exception {
        //given
        ReviewList reviewList = new ReviewList(UUID.randomUUID(), "포장이 뜯어졌어요", 2, ReviewStatus.VISIBLE);
        Page<ReviewList> reviewPage = new PageImpl<>(
            List.of(reviewList, reviewList, reviewList), PageRequest.of(0, 10), 3);

        given(reviewService.getMyReview(eq(1L), any())).willReturn(reviewPage);

        //when & then
        mockMvc.perform(
                get("/api/reviews/me")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content.length()").value(3))
            .andExpect(jsonPath("$.data.content[0].content").value("포장이 뜯어졌어요"))
            .andExpect(jsonPath("$.data.content[1].rating").value(2))
            .andExpect(jsonPath("$.data.content[2].reviewStatus").value("VISIBLE"))
            .andDo(print())
            .andDo(document("review-get-my-list",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data.content[].reviewId").type(JsonFieldType.STRING).description("리뷰 ID"),
                    fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("별점 (1~5)"),
                    fieldWithPath("data.content[].reviewStatus").type(JsonFieldType.STRING).description("리뷰 상태 (VISIBLE / HIDDEN)"),
                    fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                    fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                    fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                    fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                    fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                    fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                    fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("미페이징 여부"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                    fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                    fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                    fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                    fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                    fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                    fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("빈 페이지 여부")
                )
            ));
    }

    @Test
    void 내_리뷰_공개_범위_전환() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        Review updatedReview = Review.of("굿굿", 5, ReviewStatus.HIDDEN);

        given(reviewService.updateMyReviewStatus(any(), eq(reviewId))).willReturn(updatedReview);

        //when & then
        mockMvc.perform(
                patch("/api/reviews/me/{reviewId}/visibility", reviewId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.reviewStatus").value("HIDDEN"))
            .andDo(print())
            .andDo(document("review-toggle-visibility",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data.reviewStatus").type(JsonFieldType.STRING).description("변경된 리뷰 상태 (VISIBLE / HIDDEN)"),
                    fieldWithPath("data.message").type(JsonFieldType.STRING).description("상태 변경 결과 메시지")
                )
            ));
    }

    @Test
    void 내_리뷰_삭제() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        willDoNothing().given(reviewService).deleteMyReview(any(), eq(reviewId));

        //when & then
        mockMvc.perform(
                patch("/api/reviews/me/{reviewId}/delete", reviewId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(print())
            .andDo(document("review-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간")
                )
            ));
    }

    @Test
    void 내_리뷰_수정() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest("와 맛있다", 4);
        Review updatedReview = Review.of(request.content(), request.rating(), ReviewStatus.VISIBLE);

        given(reviewService.updateMyReview(any(), eq(reviewId), any())).willReturn(updatedReview);

        //when & then
        mockMvc.perform(
                patch("/api/reviews/me/{reviewId}", reviewId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").value("와 맛있다"))
            .andExpect(jsonPath("$.data.rating").value(4))
            .andDo(print())
            .andDo(document("review-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 리뷰 내용"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("수정할 별점 (1~5)")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("요청 결과 (SUCCESS / FAIL)"),
                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("수정된 리뷰 내용"),
                    fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("수정된 별점"),
                    fieldWithPath("data.message").type(JsonFieldType.STRING).description("처리 결과 메시지")
                )
            ));
    }
}
