package com.elice.sdz.order.dto;

import com.elice.sdz.order.entity.Order.Status;  // Order 엔티티의 Status enum 사용
import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class OrderDto {
    private Long orderId;       // 주문 ID
    //private String userId;    // 사용자 ID
    private String email;       // 사용자 email
    private int orderCount;     // 주문 수량
    private Double orderAmount; // 주문 금액
    private Status orderStatus; // 주문 상태 (ENUM)
    private Instant regDate;    // 주문 날짜
    private boolean refundStatus; // 환불 상태 여부
    private Long productId;     // 상품 ID
    private DeliveryAddressDTO deliveryAddress; //배송 주소 정보



}
