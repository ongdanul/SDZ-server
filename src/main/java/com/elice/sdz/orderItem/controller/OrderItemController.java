package com.elice.sdz.orderItem.controller;

import com.elice.sdz.orderItem.dto.OrderItemModifyDTO;
import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.orderItem.service.OrderItemService;
import com.elice.sdz.user.entity.Users;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-item")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    // 장바구니 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderItemDetail>> getOrderItems(@PathVariable String userId) {
        List<OrderItemDetail> orderItems = orderItemService.getOrderItems(userId);
        return ResponseEntity.ok(orderItems);
    }

    // 장바구니 수정
    @PostMapping("/modify/{userId}")
    public ResponseEntity<Void> modifyOrderItem(@PathVariable String userId, @RequestBody OrderItemModifyDTO request) {
        if (request.getQuantity() > 0) {
            // 추가
            orderItemService.addOrderItem(userId, request.getProduct(), request.getQuantity());
        } else {
            // 삭제 또는 수량 감소
            orderItemService.deleteOrderItem(userId, request.getProduct(), Math.abs(request.getQuantity()));
        }
        return ResponseEntity.ok().build();
    }


    // 장바구니 전체 삭제
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearOrderItems(@PathVariable String userId) {
        orderItemService.clearOrderItems(userId);
        return ResponseEntity.ok().build();
    }
}
