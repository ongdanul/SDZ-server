package com.elice.sdz.product.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class ProductDTO {


    private Long productId;
    //private Long categoryId;
    //private Long userId;
    private String productName;
    private int productCount;
    private Double productAmount;
    private String productContent;
}