package com.elice.sdz.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
public class CategoryResponseDTO {

    private Long categoryId;

    private String categoryName;

    private Long parentId;

    private List<CategoryResponseDTO> subCategories;
}
