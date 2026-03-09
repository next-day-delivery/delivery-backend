package com.nextdaydelivery.store.domain.repository;

import com.nextdaydelivery.store.domain.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    // 1. 저장
    Category save(Category category);

    // 2. ID로 단건 조회
    Optional<Category> findById(UUID categoryId);

    // 3. 카테고리 이름으로 조회 (필드명 categoryName에 맞춤)
    // 서비스 계층의 getOrCreateCategory 로직에서 필수입니다.
    Optional<Category> findByCategoryName(String categoryName);

    // 4. 이름으로 존재 여부 확인
    boolean existsByCategoryName(String categoryName);

    // 5. 여러 ID로 일괄 조회 (N+1 방지용)
    List<Category> findAllById(Iterable<UUID> ids);

    // 6. 전체 목록 조회
    List<Category> findAll();
}
