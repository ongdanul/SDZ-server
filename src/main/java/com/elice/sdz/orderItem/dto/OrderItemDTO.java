package com.elice.sdz.orderItem.dto;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderItemDTO {
    private Long orderItemId;
    private List<OrderItemDetailDTO> orderItemDetails;

    @Data
    @ToString
    public static class OrderItemDetailDTO {
        private Long productId;
        private int quantity;
        private String productName;
        private double productAmount;
        private String thumbnailPath;
    }
}