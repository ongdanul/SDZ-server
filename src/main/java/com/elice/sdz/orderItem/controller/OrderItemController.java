package com.elice.sdz.orderItem.controller;

import com.elice.sdz.orderItem.dto.OrderItemDTO;
import com.elice.sdz.orderItem.service.OrderItemService;
import com.elice.sdz.orderItem.dto.OrderItemModifyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.elice.sdz.user.service.AuthenticationService;

@RestController
@RequestMapping("/api/order-item")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final AuthenticationService authenticationService;

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<OrderItemDTO> getOrderItems() {
        String userId = authenticationService.getCurrentUser();
        OrderItemDTO orderItems = orderItemService.getOrderItems(userId);
        return ResponseEntity.ok(orderItems);
    }

    // 장바구니 수정
    @PostMapping("/modify")
    public ResponseEntity<Void> modifyOrderItem(@RequestBody OrderItemModifyDTO request) {
        String userId = authenticationService.getCurrentUser();
        if (request.getQuantity() > 0) {
            // 추가
            orderItemService.addOrderItem(userId, request.getProductId(), request.getQuantity());
        } else {
            // 삭제 또는 수량 감소
            orderItemService.deleteOrderItem(userId, request.getProductId(), Math.abs(request.getQuantity()));
        }
        return ResponseEntity.ok().build();
    }

    // 장바구니 전체 삭제
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearOrderItems() {
        String userId = authenticationService.getCurrentUser();
        orderItemService.clearOrderItems(userId);
        return ResponseEntity.ok().build();
    }
}
