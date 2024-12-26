package com.elice.sdz.category.dto;

import com.elice.sdz.category.entity.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequestDTO {

    @NotNull
    @Size(min = 1, max = 10)
    private String categoryName;

    private Long parentId;

    public Category toEntity() {
        return Category.builder()
                .categoryName(categoryName)
                .parentId(parentId)
                .build();
    }

}
