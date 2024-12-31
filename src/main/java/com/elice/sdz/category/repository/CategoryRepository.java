package com.elice.sdz.category.repository;

import com.elice.sdz.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리 이름으로 조회
    Optional<Category> findByCategoryName(String categoryName);

    // 부모 아이디로 조회
    List<Category> findByParentId(Long parentId);

    // 루트 카테고리의 개수 카운트
    int countByParentIdNull();

    // 하위 카테고리의 개수 카운트
    int countByParentId(Long parentId);
}
