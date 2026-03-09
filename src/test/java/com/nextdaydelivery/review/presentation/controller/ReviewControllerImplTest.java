package com.nextdaydelivery.review.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdaydelivery.review.application.service.ReviewService;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewControllerImpl.class)
public class ReviewControllerImplTest {

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void 가게_리뷰_전체_조회() throws Exception {
        //given
        UUID storeId = UUID.randomUUID();
        ReviewList reviewList = new ReviewList(
            UUID.randomUUID(),
            "맛있어요",
            5,
            ReviewStatus.VISIBLE
        );
        given(reviewService.getReview(storeId)).willReturn(List.of(reviewList));

        //when & then
        mockMvc.perform(
                get("/api/reviews/{storeId}", storeId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].content").value("맛있어요"))
            .andExpect(jsonPath("$.data[0].rating").value(5))
            .andExpect(jsonPath("$.data[0].reviewStatus").value("VISIBLE"))
            .andDo(print());

    }

    @Test
    @WithMockUser
    void 내_리뷰_저장() throws Exception {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest("맛없어요", 1);
        UUID orderId = UUID.randomUUID();
        Review review = Review.of(request.content(), request.rating(), ReviewStatus.VISIBLE);
        given(reviewService.saveReview(any(), eq(orderId), any())).willReturn(review);

        //when & then
        mockMvc.perform(
                post("/api/reviews/write/{orderId}", orderId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk()).andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value("맛없어요"))
            .andExpect(jsonPath("$.data.rating").value(1))
            .andExpect(jsonPath("$.data.message").value("리뷰가 저장되었습니다."))
            .andDo(print());

    }

    @Test
    @WithMockUser
    void 내_리뷰_전체_조회() throws Exception {

        //given
        ReviewList reviewList = new ReviewList(UUID.randomUUID(), "포장이 뜯어졌어요", 2, ReviewStatus.VISIBLE);
        given(reviewService.getMyReview(any())).willReturn(List.of(reviewList, reviewList, reviewList));

        //when & then
        mockMvc.perform(
                get("/api/reviews/me").param("userId", "1")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].content").value("포장이 뜯어졌어요"))
            .andExpect(jsonPath("$.data[1].rating").value(2))
            .andExpect(jsonPath("$.data[2].reviewStatus").value("VISIBLE"));
    }

    @Test
    @WithMockUser
    void 내_리뷰_공개_범위_전환() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        Review updatedReview = Review.of("굿굿", 5, ReviewStatus.HIDDEN);
        given(reviewService.updateMyReviewStatus(reviewId)).willReturn(updatedReview);

        //when & then
        mockMvc.perform(
                patch("/api/reviews/me/{reviewId}/visibility", reviewId)
                    .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath(("$.data.reviewStatus")).value("HIDDEN")
            );
    }

    @Test
    @WithMockUser
    void 내_리뷰_삭제() throws Exception {

        //given
        UUID reviewId = UUID.randomUUID();

        //when & then
        mockMvc.perform(
                delete("/api/reviews/me/{reviewId}", reviewId)
                    .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"));

    }

    @Test
    @WithMockUser
    void 내_리뷰_수정() throws Exception {

        //given
        UUID reviewId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest("와 맛있다", 4);
        Review updatedReview = Review.of(request.content(), request.rating(), ReviewStatus.VISIBLE);
        given(reviewService.updateMyReview(eq(reviewId), any())).willReturn(updatedReview);

        //when & then
        mockMvc.perform(
                patch("/api/reviews/me/{reviewId}", reviewId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").value("와 맛있다"))
            .andExpect(jsonPath("$.data.rating").value(4));
    }

}
