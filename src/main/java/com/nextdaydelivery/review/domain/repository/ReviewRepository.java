package com.nextdaydelivery.review.domain.repository;

import com.nextdaydelivery.review.domain.entity.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByStoreStoreId(UUID storeId);

    List<Review> findByUserUserId(Long userId);
}