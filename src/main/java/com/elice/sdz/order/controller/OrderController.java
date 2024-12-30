package com.elice.sdz.order.controller;

import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.service.OrderService;
import com.elice.sdz.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthenticationService authenticationService;
    //사용자 주문 목록 조회
    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        String userId = authenticationService.getCurrentUser();
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();  // 주문이 없을 때 204 No Content 반환
        }
        return ResponseEntity.ok(orders);
    }

    //특정 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    //사용자 주문 추가
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        String userId = authenticationService.getCurrentUser();
        OrderDto createdOrder = orderService.createOrder(orderDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
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
