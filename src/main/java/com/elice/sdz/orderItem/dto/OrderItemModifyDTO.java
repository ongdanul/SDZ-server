package com.elice.sdz.orderItem.dto;

import com.elice.sdz.product.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemModifyDTO {
    private Product product;
    private int quantity;
}
