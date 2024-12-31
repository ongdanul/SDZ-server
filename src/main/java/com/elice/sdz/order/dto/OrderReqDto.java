package com.elice.sdz.order.dto;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
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
//서버->클라이언트
public class OrderReqDto {
    private Double totalPrice;
    private boolean refundStatus; // 환불 상태 여부
    private String email;       // 사용자 email
    private DeliveryAddressDTO deliveryAddress;
    private List<OrderItemDTO> orderItems = new ArrayList<>();


}
