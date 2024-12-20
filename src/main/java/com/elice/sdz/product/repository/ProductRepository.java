package com.elice.sdz.product.repository;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}
