package com.elice.sdz.product.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private int productCount;
    private Double productAmount;
    private String productContent;
    private String thumbnailPath;
}
