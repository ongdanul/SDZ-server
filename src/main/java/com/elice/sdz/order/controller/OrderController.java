package com.elice.sdz.order.controller;

import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.service.OrderService;
import com.elice.sdz.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    //사용자 주문 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    //특정 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }
    //사용자 주문 추가
    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto, @PathVariable String userId) {
        return ResponseEntity.ok(orderService.createOrder(orderDto, userId));
    }
    //사용자 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long orderId, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDto));
    }
    //사용자 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    //관리자 모든 주문 조회
    @GetMapping("/admin")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }
    //관리자 주문 상태 수정
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.Status status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
    //관리자 주문 삭제
    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<Void> deleteOrderByAdmin(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
