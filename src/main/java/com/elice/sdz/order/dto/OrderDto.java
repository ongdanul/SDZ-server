package com.elice.sdz.order.dto;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.order.entity.Order.Status;  // Order 엔티티의 Status enum 사용
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
@Builder
public class OrderDto {
    private Long orderId;       // 주문 ID
    private Double totalPrice;
    private boolean refundStatus; // 환불 상태 여부
    private Instant regDate;    // 주문 날짜
    private Status orderStatus; // 주문 상태 (ENUM)
    private Integer orderCount;  // OrderDetail의 주문 수량
    private Double orderAmount;  // OrderDetail의 주문 금액
    private Long productId;
    private String email;       // 사용자 email
    private DeliveryAddress deliveryAddress;
    private List<OrderItemDTO> orderItems = new ArrayList<>();

}
