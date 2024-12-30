package com.elice.sdz.orderItem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemModifyDTO {
    private Long productId;
    private int quantity;
}
