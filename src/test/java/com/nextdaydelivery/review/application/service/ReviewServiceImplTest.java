package com.nextdaydelivery.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.user.domain.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Test
    void 가게_리뷰_전체_조회() {

        //given
        UUID storeId = UUID.randomUUID();
//        Review review = Review.builder()
//            .content("맛이 훌륭해요. 배달이 빨라요")
//            .rating(5)
//            .reviewStatus(ReviewStatus.VISIBLE)
//            .build();
        Review review = Review.of("맛이 훌륭해요. 배달이 빨라요", 5, ReviewStatus.VISIBLE);

        given(reviewRepository.findByStoreStoreId(storeId))
            .willReturn(List.of(review));

        //when
        List<ReviewList> result = reviewService.getReview(storeId);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().content()).isEqualTo("맛이 훌륭해요. 배달이 빨라요");
        assertThat(result.getFirst().rating()).isEqualTo(5);
        assertThat(result.getFirst().reviewStatus()).isEqualTo(ReviewStatus.VISIBLE);
        verify(reviewRepository).findByStoreStoreId(storeId);
    }

    @Test
    void 내_리뷰_저장() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest("음식이 간이 세요", 2);
        UUID orderId = UUID.randomUUID();
        User user = User.builder().build();
        Order order = mock(Order.class);
        Store store = mock(Store.class);

        given(orderRepository.findById(orderId))
            .willReturn(Optional.of(order));
        given(order.getStore())
            .willReturn(store);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        //when
        reviewService.saveReview(request, orderId, user);

        //then
        verify(orderRepository).findById(orderId);
        verify(order).getStore();
        verify(reviewRepository).save(captor.capture());

        Review savedReview = captor.getValue();
        assertThat(savedReview.getContent()).isEqualTo("음식이 간이 세요");
        assertThat(savedReview.getRating()).isEqualTo(2);
        assertThat(savedReview.getReviewStatus()).isEqualTo(ReviewStatus.VISIBLE);
    }

    @Test
    void 내_리뷰_저장_해당하는_주문없음_예외발생() {

        //given
        UUID orderId = UUID.randomUUID();
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());
        ReviewCreateRequest request = new ReviewCreateRequest("에러", 0);

        //when & then
        assertThatThrownBy(() -> reviewService.saveReview(request, orderId, User.builder().build()))
            .isInstanceOf(NoSuchElementException.class);
        verify(orderRepository).findById(orderId);
    }


    @Test
    void 내_리뷰_조회() {

        //given
        Review review1 = Review.of("맛있어요", 5, ReviewStatus.VISIBLE);
        
        Review review2 = Review.builder()
            .content("별로예요")
            .rating(1).reviewStatus(ReviewStatus.HIDDEN)
            .build();

        Long userId = 1L;

        given(reviewRepository.findByUserUserId(userId)).willReturn(List.of(review1, review2));

        //when
        List<ReviewList> result = reviewService.getMyReview(userId);

        //then
        verify(reviewRepository).findByUserUserId(userId);
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().content()).isEqualTo("맛있어요");
        assertThat(result.getFirst().rating()).isEqualTo(5);
        assertThat(result.getFirst().reviewStatus()).isEqualTo(ReviewStatus.VISIBLE);
        assertThat(result.get(1).content()).isEqualTo("별로예요");
        assertThat(result.get(1).rating()).isEqualTo(1);
        assertThat(result.get(1).reviewStatus()).isEqualTo(ReviewStatus.HIDDEN);

    }

    @Test
    void 내_리뷰_공개범위_전환() {

        //given
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder()
            .reviewId(reviewId)
            .reviewStatus(ReviewStatus.VISIBLE)
            .build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        //when
        reviewService.updateMyReviewStatus(reviewId);
        //then
        verify(reviewRepository).findById(reviewId);
        assertThat(review.getReviewStatus()).isEqualTo(ReviewStatus.HIDDEN);

    }

    @Test
    void 내_리뷰_공개범위_전환_리뷰없음_예외발생() {

        //given
        UUID wrongReviewId = UUID.randomUUID();
        given(reviewRepository.findById(wrongReviewId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.updateMyReviewStatus(wrongReviewId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("리뷰를 찾을 수 없습니다.");

        verify(reviewRepository).findById(wrongReviewId);

    }

    @Test
    void 내_리뷰_수정() {

        //given
        ReviewCreateRequest request = new ReviewCreateRequest("너무 맛있습니다.", 5);
        UUID reviewId = UUID.randomUUID();

        Review review = Review.builder()
            .content("너너무무무 맛있습습습니니다.")
            .rating(3)
            .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        Review result = reviewService.updateMyReview(reviewId, request);

        //then
        verify(reviewRepository).findById(reviewId);

        assertThat(result.getContent()).isEqualTo(review.getContent());
        assertThat(result.getRating()).isEqualTo(review.getRating());
    }

    @Test
    void 내_리뷰_수정_리뷰없음_예외발생() {

        //given
        UUID reviewId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest("리뷰 수정 전", 4);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.updateMyReview(reviewId, request))
            .isInstanceOf(NoSuchElementException.class);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void 내_리뷰_삭제() {

        //given
        UUID reviewId = UUID.randomUUID();
        //when
        reviewService.deleteMyReview(reviewId);
        //then
        verify(reviewRepository).deleteById(reviewId);
    }
}
