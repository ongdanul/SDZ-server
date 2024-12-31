package com.elice.sdz.order.dto;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.order.entity.Order.Status;
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
@Builder
//클라이언트->서버
public class OrderResDto {
    private Long orderId;       // 주문 ID
    private Double totalPrice;
    private boolean refundStatus; // 환불 상태 여부
    private LocalDateTime regDate;    // 주문 날짜
    private Status orderStatus; // 주문 상태 (ENUM)
    private String email;       // 사용자 email
    private DeliveryAddressDTO deliveryAddress; // 배송 주소 추가

    @Builder.Default
    private List<OrderItemDTO> orderItems = new ArrayList<>();
}
