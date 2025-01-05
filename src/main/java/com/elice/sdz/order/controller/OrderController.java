package com.elice.sdz.order.controller;


import com.elice.sdz.order.dto.OrderReqDto;
import com.elice.sdz.order.dto.OrderResDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.service.OrderService;
import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthenticationService authenticationService;
    //사용자 주문 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<OrderResDto>> getUserOrders(PageRequestDTO pageRequestDTO) {
        String userId = authenticationService.getCurrentUser();
        PageResponseDTO<OrderResDto> ordersPage = orderService.getOrdersByUserId(userId, pageRequestDTO);

        if (ordersPage.getTotal() <= 0) {
            return ResponseEntity.noContent().build();  // 주문이 없을 경우 204 No Content 반환
        }

        return ResponseEntity.ok(ordersPage);
    }
    //특정 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResDto> getOrderDetail(@PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    //사용자 주문 추가
    @PostMapping
    public ResponseEntity<OrderResDto> createOrder(@RequestBody OrderReqDto orderReqDto) {
        String userId = authenticationService.getCurrentUser();
        OrderResDto createdOrder = orderService.createOrder(orderReqDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    //사용자 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResDto> updateOrder(@PathVariable Long orderId, @RequestBody OrderReqDto orderReqDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderReqDto));
    }
    //사용자 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    //관리자 모든 주문 조회
    @GetMapping("/admin")
    public ResponseEntity<PageResponseDTO<OrderResDto>> getAllOrders(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<OrderResDto> orders = orderService.findAllOrders(pageRequestDTO);
        return ResponseEntity.ok(orders);
    }
    //관리자 주문 상태 수정
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Order.Status status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
    //관리자 주문 삭제
    @DeleteMapping("/admin/orders")
    public ResponseEntity<Void> deleteOrdersByAdmin(@RequestBody List<Long> orderIds) {
        orderService.deleteOrdersByAdmin(orderIds);
        return ResponseEntity.ok().build();
    }

}
