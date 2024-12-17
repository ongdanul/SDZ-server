package com.elice.sdz.category.entity;

import com.elice.sdz.category.dto.CategoryResponseDTO;
import com.elice.sdz.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "category_name", length = 30, nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryResponseDTO toResponseDTO() {
        return CategoryResponseDTO.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .build();
    }
}
