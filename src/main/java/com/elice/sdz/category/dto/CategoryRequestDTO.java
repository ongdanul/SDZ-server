package com.elice.sdz.category.dto;

import com.elice.sdz.category.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequestDTO {

    private String categoryName;

    public Category toEntity() {
        return new Category(this.categoryName);
    }

}
