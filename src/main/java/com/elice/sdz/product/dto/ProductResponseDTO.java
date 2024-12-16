package com.elice.sdz.product.dto;


import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private String userName;
    private String categoryName;
}
