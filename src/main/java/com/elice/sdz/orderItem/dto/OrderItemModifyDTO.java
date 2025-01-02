package com.elice.sdz.orderItem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItemModifyDTO {
    private Long productId;
    private int quantity;
}
