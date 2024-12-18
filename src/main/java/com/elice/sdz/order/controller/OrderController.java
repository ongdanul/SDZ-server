package com.elice.sdz.order.controller;

import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.service.OrderService;
import com.elice.sdz.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    //사용자 주문 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Users userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    //특정 주문 상세 조회

    //사용자 주문 추가
    @PostMapping("/user/{userId}") // 새 주문
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto, @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.createOrder(orderDto, userId));
    }
    //사용자 주문 수정
    @PutMapping("/{orderId}") // 주문 업데이트
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long orderId, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDto));
    }
    //사용자 주문 취소
    @DeleteMapping("/{orderId}") // 주문 삭제
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    //관리자 모든 주문 조회

    //관리자 주문 상태 수정

    //관리자 주문 삭제

}
