package com.elice.sdz.category.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryResponseDTO {

    private Long categoryId;

    private String categoryName;

    private Long parentId;

    private List<CategoryResponseDTO> subCategories;
}
