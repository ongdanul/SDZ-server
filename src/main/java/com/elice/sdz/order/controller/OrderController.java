package com.elice.sdz.order.controller;

import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.service.OrderService;
import com.elice.sdz.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/admin") // 전체 주문 조회
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @GetMapping("/user/{userId}") //사용자의 모든 주문 목록
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Users userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }


    @PostMapping("/user/{userId}") // 새 주문
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto, @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.createOrder(orderDto, userId));
    }

    @PutMapping("/{orderId}") // 주문 업데이트
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long orderId, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDto));
    }

    @DeleteMapping("/{orderId}") // 주문 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
