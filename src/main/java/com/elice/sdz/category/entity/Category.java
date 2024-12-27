package com.elice.sdz.category.entity;

import com.elice.sdz.category.dto.CategoryResponseDTO;
import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "category_name", length = 10, nullable = false)
    private String categoryName;

    @Column(name = "parent_id", nullable = true)
    private Long parentId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,
            orphanRemoval = false, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    public CategoryResponseDTO toResponseDTO() {
        return CategoryResponseDTO.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .parentId(parentId)
                .build();
    }
}
