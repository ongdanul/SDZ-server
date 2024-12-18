package com.elice.sdz.order.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.repository.OrderRepository;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private Long users;



    @Transactional(readOnly = true)//읽기전용
    public List<OrderDto> getAllOrders() { //모든 주문조회
        return orderRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Users userId) {
        Users user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return orderRepository.findById(users).stream().map(this::toDto).collect(Collectors.toList());
    }
    //사용자 주문 추가
    @Transactional
    public OrderDto createOrder(OrderDto orderDto, Long userId) {
        Users user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Order order = toEntity(orderDto);
        order.setUser(user);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }
    //사용자 주문 수정
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus() != Order.Status.PENDING) {
            throw new CustomException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }

        order.setOrderCount(orderDto.getOrderCount());
        order.setOrderAmount(orderDto.getOrderAmount());
        order.setRefundStatus(orderDto.isRefundStatus());

        Order updatedOrder = orderRepository.save(order);
        return toDto(updatedOrder);
    }



    //사용자 주문취소
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));



        orderRepository.delete(order);
    }
    //관리자 주문 상태수정
    @Transactional
    public OrderDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.setOrderStatus(status);
        return toDto(orderRepository.save(order));
    }
    //관리자 주문 취소
    //관리자 모든 주문 조회
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser());  // Users 엔티티의 ID를 설정
        dto.setOrderCount(order.getOrderCount());
        dto.setOrderAmount(order.getOrderAmount());
        dto.setRefundStatus(order.isRefundStatus());
        return dto;
    }

    private Order toEntity(OrderDto dto) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderCount(dto.getOrderCount());
        order.setOrderAmount(dto.getOrderAmount());
        order.setRefundStatus(dto.isRefundStatus());
        return order;
    }



}
