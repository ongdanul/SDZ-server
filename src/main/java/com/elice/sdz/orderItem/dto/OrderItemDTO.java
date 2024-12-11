package com.elice.sdz.orderItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    @NotNull(message = "")
    @Size(min = 4, max = 50, message = "")
    private String userId;

    @NotNull(message = "")
    private Long productId;

    private Long orderId;

    @NotNull(message = "")
    @Min(value = 1, message = "")
    private int orderItemCount;

    @NotNull(message = "")
    private Timestamp regDate;
}
