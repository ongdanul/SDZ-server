package com.elice.sdz.orderItem.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long orderItemId;
    private List<OrderItemDetailDTO> orderItemDetails;

    @Data
    public static class OrderItemDetailDTO {
        private Long productId;
        private int quantity;
        private double productAmount;
    }
}