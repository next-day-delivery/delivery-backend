package com.nextdaydelivery.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.nextdaydelivery.global.security.dto.AuthUserDto;
import com.nextdaydelivery.order.domain.entity.Order;
import com.nextdaydelivery.order.domain.repository.OrderRepository;
import com.nextdaydelivery.review.domain.entity.Review;
import com.nextdaydelivery.review.domain.entity.enums.ReviewStatus;
import com.nextdaydelivery.review.domain.repository.ReviewRepository;
import com.nextdaydelivery.review.presentation.dto.request.ReviewCreateRequest;
import com.nextdaydelivery.review.presentation.dto.response.ReviewList;
import com.nextdaydelivery.store.domain.entity.Store;
import com.nextdaydelivery.store.domain.service.StoreReviewService;
import com.nextdaydelivery.user.domain.entity.User;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;
import com.nextdaydelivery.user.domain.repository.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreReviewService storeReviewService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Test
    void 가게_리뷰_전체_조회() {
        //given
        UUID storeId = UUID.randomUUID();
        Review review = Review.of("맛이 훌륭해요. 배달이 빨라요", 5, ReviewStatus.VISIBLE);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        given(reviewRepository.findByStoreStoreIdAndReviewStatus(storeId, ReviewStatus.VISIBLE, pageable)).willReturn(
            reviewPage);

        //when
        Page<ReviewList> result = reviewService.getReview(storeId, pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().content()).isEqualTo("맛이 훌륭해요. 배달이 빨라요");
        assertThat(result.getContent().getFirst().rating()).isEqualTo(5);
        assertThat(result.getContent().getFirst().reviewStatus()).isEqualTo(ReviewStatus.VISIBLE);
        verify(reviewRepository).findByStoreStoreIdAndReviewStatus(storeId, ReviewStatus.VISIBLE, pageable);
    }

    @Test
    void 내_리뷰_저장() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest("음식이 간이 세요", 2);
        UUID orderId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        User user = mock(User.class);
        Order order = mock(Order.class);
        Store store = mock(Store.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(order.getStore()).willReturn(store);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        //when
        reviewService.saveReview(request, orderId, authUser);

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
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        ReviewCreateRequest request = new ReviewCreateRequest("에러", 1);

        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.saveReview(request, orderId, authUser))
            .isInstanceOf(NoSuchElementException.class);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void 내_리뷰_조회() {
        //given
        Review review1 = Review.of("맛있어요", 5, ReviewStatus.VISIBLE);
        Review review2 = Review.builder()
            .content("별로예요")
            .rating(1)
            .reviewStatus(ReviewStatus.HIDDEN)
            .build();
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review1, review2), pageable, 2);

        given(reviewRepository.findByUserUserId(userId, pageable)).willReturn(reviewPage);

        //when
        Page<ReviewList> result = reviewService.getMyReview(userId, pageable);

        //then
        verify(reviewRepository).findByUserUserId(userId, pageable);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).content()).isEqualTo("맛있어요");
        assertThat(result.getContent().get(0).rating()).isEqualTo(5);
        assertThat(result.getContent().get(0).reviewStatus()).isEqualTo(ReviewStatus.VISIBLE);
        assertThat(result.getContent().get(1).content()).isEqualTo("별로예요");
        assertThat(result.getContent().get(1).rating()).isEqualTo(1);
        assertThat(result.getContent().get(1).reviewStatus()).isEqualTo(ReviewStatus.HIDDEN);
    }

    @Test
    void 내_리뷰_공개범위_전환() {
        //given
        UUID reviewId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        User user = mock(User.class);
        given(user.getUserId()).willReturn(1L);

        Review review = Review.builder()
            .reviewId(reviewId)
            .user(user)
            .reviewStatus(ReviewStatus.VISIBLE)
            .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        reviewService.updateMyReviewStatus(authUser, reviewId);

        //then
        verify(reviewRepository).findById(reviewId);
        assertThat(review.getReviewStatus()).isEqualTo(ReviewStatus.HIDDEN);
    }

    @Test
    void 내_리뷰_공개범위_전환_리뷰없음_예외발생() {
        //given
        UUID wrongReviewId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        given(reviewRepository.findById(wrongReviewId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.updateMyReviewStatus(authUser, wrongReviewId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("리뷰를 찾을 수 없습니다.");

        verify(reviewRepository).findById(wrongReviewId);
    }

    @Test
    void 내_리뷰_수정() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest("너무 맛있습니다.", 5);
        UUID reviewId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        User user = mock(User.class);
        given(user.getUserId()).willReturn(1L);

        Review review = Review.builder()
            .content("너너무무무 맛있습습습니니다.")
            .rating(3)
            .user(user)
            .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        Review result = reviewService.updateMyReview(authUser, reviewId, request);

        //then
        verify(reviewRepository).findById(reviewId);
        assertThat(result.getContent()).isEqualTo("너무 맛있습니다.");
        assertThat(result.getRating()).isEqualTo(5);
    }

    @Test
    void 내_리뷰_수정_리뷰없음_예외발생() {
        //given
        UUID reviewId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        ReviewCreateRequest request = new ReviewCreateRequest("리뷰 수정 전", 4);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.updateMyReview(authUser, reviewId, request))
            .isInstanceOf(NoSuchElementException.class);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void 내_리뷰_삭제() {
        //given
        UUID reviewId = UUID.randomUUID();
        AuthUserDto authUser = new AuthUserDto(1L, UserRole.CUSTOMER);
        User user = mock(User.class);
        Store store = mock(Store.class);
        given(user.getUserId()).willReturn(1L);

        Review review = mock(Review.class);
        given(review.getUser()).willReturn(user);
        given(review.getStore()).willReturn(store);
        given(review.getRating()).willReturn(3);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        reviewService.deleteMyReview(authUser, reviewId);

        //then
        verify(storeReviewService).minusReviewSummary(store, 3);
        verify(review).delete(authUser.userId().toString());
    }
}
