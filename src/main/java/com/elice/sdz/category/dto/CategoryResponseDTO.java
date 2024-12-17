package com.elice.sdz.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponseDTO {

    private Long categoryId;

    private String categoryName;
}
